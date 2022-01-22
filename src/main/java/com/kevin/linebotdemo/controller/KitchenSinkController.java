/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.kevin.linebotdemo.controller;

import com.kevin.linebotdemo.model.dto.BusDto;
import com.kevin.linebotdemo.model.dto.QueryDto;
import com.linecorp.bot.client.LineBlobClient;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.DatetimePickerAction;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.*;
import com.linecorp.bot.model.event.message.FileMessageContent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.group.GroupMemberCountResponse;
import com.linecorp.bot.model.group.GroupSummaryResponse;
import com.linecorp.bot.model.message.*;
import com.linecorp.bot.model.message.imagemap.*;
import com.linecorp.bot.model.message.sender.Sender;
import com.linecorp.bot.model.message.template.*;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.model.room.RoomMemberCountResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;

@Slf4j
@LineMessageHandler
@AllArgsConstructor
public class KitchenSinkController {
    private final LineMessagingClient lineMessagingClient;

    private final LineBlobClient lineBlobClient;
    private final BusEstimatedController busEstimatedController;

    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {
        TextMessageContent message = event.getMessage();
        handleTextContent(event.getReplyToken(), event, message);
    }

    @EventMapping
    public void handleStickerMessageEvent(MessageEvent<StickerMessageContent> event) {
        handleSticker(event.getReplyToken(), event.getMessage());
    }

    @EventMapping
    public void handleLocationMessageEvent(MessageEvent<LocationMessageContent> event) {
        LocationMessageContent locationMessage = event.getMessage();
        reply(event.getReplyToken(), new LocationMessage(
                locationMessage.getTitle(),
                locationMessage.getAddress(),
                locationMessage.getLatitude(),
                locationMessage.getLongitude()
        ));
    }


    @EventMapping
    public void handleVideoPlayCompleteEvent(VideoPlayCompleteEvent event) throws IOException {
        log.info("Got video play complete: tracking id={}", event.getVideoPlayComplete().getTrackingId());
        this.replyText(event.getReplyToken(),
                "You played " + event.getVideoPlayComplete().getTrackingId());
    }

    @EventMapping
    public void handleFileMessageEvent(MessageEvent<FileMessageContent> event) {
        this.reply(event.getReplyToken(),
                new TextMessage(String.format("Received '%s'(%d bytes)",
                        event.getMessage().getFileName(),
                        event.getMessage().getFileSize())));
    }

    @EventMapping
    public void handleUnfollowEvent(UnfollowEvent event) {
        log.info("unfollowed this bot: {}", event);
    }

    @EventMapping
    public void handleUnknownEvent(UnknownEvent event) {
        log.info("Got an unknown event!!!!! : {}", event);
    }

    @EventMapping
    public void handleFollowEvent(FollowEvent event) {
        String replyToken = event.getReplyToken();
        this.replyText(replyToken, "Got followed event");
    }

    @EventMapping
    public void handleJoinEvent(JoinEvent event) {
        String replyToken = event.getReplyToken();
        this.replyText(replyToken, "Joined " + event.getSource());
    }

    @EventMapping
    public void handlePostbackEvent(PostbackEvent event) {
        String replyToken = event.getReplyToken();
        this.replyText(replyToken,
                "Got postback data " + event.getPostbackContent().getData() + ", param " + event
                        .getPostbackContent().getParams().toString());
    }

    @EventMapping
    public void handleBeaconEvent(BeaconEvent event) {
        String replyToken = event.getReplyToken();
        this.replyText(replyToken, "Got beacon message " + event.getBeacon().getHwid());
    }

    @EventMapping
    public void handleMemberJoined(MemberJoinedEvent event) {
        String replyToken = event.getReplyToken();
        this.replyText(replyToken, "Got memberJoined message " + event.getJoined().getMembers()
                .stream().map(Source::getUserId)
                .collect(Collectors.joining(",")));
    }

    @EventMapping
    public void handleMemberLeft(MemberLeftEvent event) {
        log.info("Got memberLeft message: {}", event.getLeft().getMembers()
                .stream().map(Source::getUserId)
                .collect(Collectors.joining(",")));
    }

    @EventMapping
    public void handleMemberLeft(UnsendEvent event) {
        log.info("Got unsend event: {}", event);
    }

    @EventMapping
    public void handleOtherEvent(Event event) {
        log.info("Received message(Ignored): {}", event);
    }

    private void reply(@NonNull String replyToken, @NonNull Message message) {
        reply(replyToken, singletonList(message));
    }

    private void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
        reply(replyToken, messages, false);
    }

    private void reply(@NonNull String replyToken,
                       @NonNull List<Message> messages,
                       boolean notificationDisabled) {
        try {
            BotApiResponse apiResponse = lineMessagingClient
                    .replyMessage(new ReplyMessage(replyToken, messages, notificationDisabled))
                    .get();
            log.info("Sent messages: {}", apiResponse);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private void replyText(@NonNull String replyToken, @NonNull String message) {
        if (replyToken.isEmpty()) {
            throw new IllegalArgumentException("replyToken must not be empty");
        }
        if (message.length() > 1000) {
            message = message.substring(0, 1000 - 2) + "……";
        }
        this.reply(replyToken, new TextMessage(message));
    }

    private void handleHeavyContent(String replyToken, String messageId,
                                    Consumer<MessageContentResponse> messageConsumer) {
        final MessageContentResponse response;
        try {
            response = lineBlobClient.getMessageContent(messageId)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            reply(replyToken, new TextMessage("Cannot get image: " + e.getMessage()));
            throw new RuntimeException(e);
        }
        messageConsumer.accept(response);
    }

    private void handleSticker(String replyToken, StickerMessageContent content) {
        reply(replyToken, new StickerMessage(
                content.getPackageId(), content.getStickerId())
        );
    }

    private void handleTextContent(String replyToken, Event event, TextMessageContent content)
            throws Exception {
        final String text = content.getText();

        log.info("Got text message from replyToken:{}: text:{} emojis:{}", replyToken, text,
                content.getEmojis());
        switch (text) {
            case "說明": {
                // TODO
                break;
            }
            case "上班": {
                log.info("line輸入：" + text);
                final String userId = event.getSource().getUserId();
                lineMessagingClient
                        .getProfile(userId)
                        .whenComplete((profile, throwable) -> {

                            if (throwable != null) {
                                this.replyText(replyToken, throwable.getMessage());
                                return;
                            }
                            try {
                                QueryDto path1 = new QueryDto("8663", "RouteName/Zh_tw eq '藍36'");
                                QueryDto path2 = new QueryDto("966", "RouteName/Zh_tw eq '民權幹線'");
                                List<BusDto> busInfo = Stream.concat(busEstimatedController.getBusInfo(path1).stream(), busEstimatedController.getBusInfo(path2).stream())
                                        .collect(Collectors.toList());
                                List<Message> textMessage;

                                if (busInfo.size() != 0) {
                                    textMessage = busInfo.stream().map(bus -> {
                                        String info = String.format("%s：預估%d分%d秒到達%s",
                                                bus.getRouteName().getZh_tw(),
                                                bus.getEstimateTime() / 60,
                                                bus.getEstimateTime() % 60,
                                                bus.getStopName().getZh_tw());
                                        return new TextMessage(info);
                                    }).collect(Collectors.toList());
                                } else {
                                    textMessage = List.of(new TextMessage("查無資料"));
                                }
                                this.reply(replyToken, textMessage);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        });
                break;
            }
            case "profile": {
                log.info("Invoking 'profile' command: source:{}",
                        event.getSource());
                final String userId = event.getSource().getUserId();
                if (userId != null) {
                    if (event.getSource() instanceof GroupSource) {
                        lineMessagingClient
                                .getGroupMemberProfile(((GroupSource) event.getSource()).getGroupId(), userId)
                                .whenComplete((profile, throwable) -> {
                                    if (throwable != null) {
                                        this.replyText(replyToken, throwable.getMessage());
                                        return;
                                    }

                                    this.reply(
                                            replyToken,
                                            Arrays.asList(new TextMessage("(from group)"),
                                                    new TextMessage(
                                                            "Display name: " + profile.getDisplayName()),
                                                    new ImageMessage(profile.getPictureUrl(),
                                                            profile.getPictureUrl()))
                                    );
                                });
                    } else {
                        lineMessagingClient
                                .getProfile(userId)
                                .whenComplete((profile, throwable) -> {

                                    if (throwable != null) {
                                        this.replyText(replyToken, throwable.getMessage());
                                        return;
                                    }

                                    List<Message> messages = Arrays.asList(new TextMessage(
                                                    "Display name: " + profile.getDisplayName()),
                                            new TextMessage("Status message: "
                                                    + profile.getStatusMessage()),
                                            new TextMessage("profile.getLanguage: " + profile.getLanguage()),
                                            new TextMessage("" + profile.getPictureUrl().toString()));
                                    this.reply(
                                            replyToken,
                                            messages);
                                });
                    }
                } else {
                    this.replyText(replyToken, "Bot can't use profile API without user ID");
                }
                break;
            }
            case "bye": {
                Source source = event.getSource();
                if (source instanceof GroupSource) {
                    this.replyText(replyToken, "Leaving group");
                    lineMessagingClient.leaveGroup(((GroupSource) source).getGroupId()).get();
                } else if (source instanceof RoomSource) {
                    this.replyText(replyToken, "Leaving room");
                    lineMessagingClient.leaveRoom(((RoomSource) source).getRoomId()).get();
                } else {
                    this.replyText(replyToken, "Bot can't leave from 1:1 chat");
                }
                break;
            }
            case "group_summary": {
                Source source = event.getSource();
                if (source instanceof GroupSource) {
                    GroupSummaryResponse groupSummary = lineMessagingClient.getGroupSummary(
                            ((GroupSource) source).getGroupId()).get();
                    this.replyText(replyToken, "Group summary: " + groupSummary);
                } else {
                    this.replyText(replyToken, "You can't use 'group_summary' command for "
                            + source);
                }
                break;
            }
            case "group_member_count": {
                Source source = event.getSource();
                if (source instanceof GroupSource) {
                    GroupMemberCountResponse groupMemberCountResponse = lineMessagingClient.getGroupMemberCount(
                            ((GroupSource) source).getGroupId()).get();
                    this.replyText(replyToken, "Group member count: "
                            + groupMemberCountResponse.getCount());
                } else {
                    this.replyText(replyToken, "You can't use 'group_member_count' command  for "
                            + source);
                }
                break;
            }
            case "room_member_count": {
                Source source = event.getSource();
                if (source instanceof RoomSource) {
                    RoomMemberCountResponse roomMemberCountResponse = lineMessagingClient.getRoomMemberCount(
                            ((RoomSource) source).getRoomId()).get();
                    this.replyText(replyToken, "Room member count: "
                            + roomMemberCountResponse.getCount());
                } else {
                    this.replyText(replyToken, "You can't use 'room_member_count' command  for "
                            + source);
                }
                break;
            }
            case "confirm": {
                ConfirmTemplate confirmTemplate = new ConfirmTemplate(
                        "Do it?",
                        new MessageAction("Yes", "Yes!"),
                        new MessageAction("No", "No!")
                );
                TemplateMessage templateMessage = new TemplateMessage("Confirm alt text", confirmTemplate);
                this.reply(replyToken, templateMessage);
                break;
            }
            case "buttons": {
                URI imageUrl = createUri("/static/buttons/1040.jpg");
                ButtonsTemplate buttonsTemplate = new ButtonsTemplate(
                        imageUrl,
                        "My button sample",
                        "Hello, my button",
                        Arrays.asList(
                                new URIAction("Go to line.me",
                                        URI.create("https://line.me"), null),
                                new PostbackAction("Say hello1",
                                        "hello こんにちは"),
                                new PostbackAction("言 hello2",
                                        "hello こんにちは",
                                        "hello こんにちは"),
                                new MessageAction("Say message",
                                        "Rice=米")
                        ));
                TemplateMessage templateMessage = new TemplateMessage("Button alt text", buttonsTemplate);
                this.reply(replyToken, templateMessage);
                break;
            }
            case "carousel": {
                URI imageUrl = createUri("/static/buttons/1040.jpg");
                CarouselTemplate carouselTemplate = new CarouselTemplate(
                        Arrays.asList(
                                new CarouselColumn(imageUrl, "hoge", "fuga", Arrays.asList(
                                        new URIAction("Go to line.me",
                                                URI.create("https://line.me"), null),
                                        new URIAction("Go to line.me",
                                                URI.create("https://line.me"), null),
                                        new PostbackAction("Say hello1",
                                                "hello こんにちは")
                                )),
                                new CarouselColumn(imageUrl, "hoge", "fuga", Arrays.asList(
                                        new PostbackAction("言 hello2",
                                                "hello こんにちは",
                                                "hello こんにちは"),
                                        new PostbackAction("言 hello2",
                                                "hello こんにちは",
                                                "hello こんにちは"),
                                        new MessageAction("Say message",
                                                "Rice=米")
                                )),
                                new CarouselColumn(imageUrl, "Datetime Picker",
                                        "Please select a date, time or datetime", Arrays.asList(
                                        DatetimePickerAction.OfLocalDatetime
                                                .builder()
                                                .label("Datetime")
                                                .data("action=sel")
                                                .initial(LocalDateTime.parse("2017-06-18T06:15"))
                                                .min(LocalDateTime.parse("1900-01-01T00:00"))
                                                .max(LocalDateTime.parse("2100-12-31T23:59"))
                                                .build(),
                                        DatetimePickerAction.OfLocalDate
                                                .builder()
                                                .label("Date")
                                                .data("action=sel&only=date")
                                                .initial(LocalDate.parse("2017-06-18"))
                                                .min(LocalDate.parse("1900-01-01"))
                                                .max(LocalDate.parse("2100-12-31"))
                                                .build(),
                                        DatetimePickerAction.OfLocalTime
                                                .builder()
                                                .label("Time")
                                                .data("action=sel&only=time")
                                                .initial(LocalTime.parse("06:15"))
                                                .min(LocalTime.parse("00:00"))
                                                .max(LocalTime.parse("23:59"))
                                                .build()
                                ))
                        ));
                TemplateMessage templateMessage = new TemplateMessage("Carousel alt text", carouselTemplate);
                this.reply(replyToken, templateMessage);
                break;
            }
            case "image_carousel": {
                URI imageUrl = createUri("/static/buttons/1040.jpg");
                ImageCarouselTemplate imageCarouselTemplate = new ImageCarouselTemplate(
                        Arrays.asList(
                                new ImageCarouselColumn(imageUrl,
                                        new URIAction("Goto line.me",
                                                URI.create("https://line.me"), null)
                                ),
                                new ImageCarouselColumn(imageUrl,
                                        new MessageAction("Say message",
                                                "Rice=米")
                                ),
                                new ImageCarouselColumn(imageUrl,
                                        new PostbackAction("言 hello2",
                                                "hello こんにちは",
                                                "hello こんにちは")
                                )
                        ));
                TemplateMessage templateMessage = new TemplateMessage("ImageCarousel alt text",
                        imageCarouselTemplate);
                this.reply(replyToken, templateMessage);
                break;
            }
            case "imagemap":
                this.reply(replyToken, ImagemapMessage
                        .builder()
                        .baseUrl(createUri("/static/rich"))
                        .altText("This is alt text")
                        .baseSize(new ImagemapBaseSize(1040, 1040))
                        .actions(Arrays.asList(
                                URIImagemapAction.builder()
                                        .linkUri("https://store.line.me/family/manga/en")
                                        .area(new ImagemapArea(0, 0, 520, 520))
                                        .build(),
                                URIImagemapAction.builder()
                                        .linkUri("https://store.line.me/family/music/en")
                                        .area(new ImagemapArea(520, 0, 520, 520))
                                        .build(),
                                URIImagemapAction.builder()
                                        .linkUri("https://store.line.me/family/play/en")
                                        .area(new ImagemapArea(0, 520, 520, 520))
                                        .build(),
                                MessageImagemapAction.builder()
                                        .text("URANAI!")
                                        .area(new ImagemapArea(520, 520, 520, 520))
                                        .build()
                        ))
                        .build());
                break;
            case "imagemap_video":
                this.reply(replyToken, ImagemapMessage
                        .builder()
                        .baseUrl(createUri("/static/imagemap_video"))
                        .altText("This is an imagemap with video")
                        .baseSize(new ImagemapBaseSize(722, 1040))
                        .video(
                                ImagemapVideo.builder()
                                        .originalContentUrl(
                                                createUri("/static/imagemap_video/originalContent.mp4"))
                                        .previewImageUrl(
                                                createUri("/static/imagemap_video/previewImage.jpg"))
                                        .area(new ImagemapArea(40, 46, 952, 536))
                                        .externalLink(
                                                new ImagemapExternalLink(
                                                        URI.create("https://example.com/see_more.html"),
                                                        "See More")
                                        )
                                        .build()
                        )
                        .actions(singletonList(
                                MessageImagemapAction.builder()
                                        .text("NIXIE CLOCK")
                                        .area(new ImagemapArea(260, 600, 450, 86))
                                        .build()
                        ))
                        .build());
                break;
            case "flex":
                break;
            case "quickreply":
                break;
            case "no_notify":
                this.reply(replyToken,
                        singletonList(new TextMessage("This message is send without a push notification")),
                        true);
                break;
            case "icon":
                this.reply(replyToken,
                        TextMessage.builder()
                                .text("Hello, I'm cat! Meow~")
                                .sender(Sender.builder()
                                        .name("Cat")
                                        .iconUrl(createUri("/static/icon/cat.png"))
                                        .build())
                                .build());
                break;
            default:
                log.info("Returns echo message {}: {}", replyToken, text);
                this.replyText(
                        replyToken,
                        text
                );
                break;
        }
    }

    private static URI createUri(String path) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .scheme("https")
                .path(path).build()
                .toUri();
    }

    private void system(String... args) {
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        try {
            Process start = processBuilder.start();
            int i = start.waitFor();
            log.info("result: {} =>  {}", Arrays.toString(args), i);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            log.info("Interrupted", e);
            Thread.currentThread().interrupt();
        }
    }


    @Value
    private static class DownloadedContent {
        Path path;
        URI uri;
    }
}
