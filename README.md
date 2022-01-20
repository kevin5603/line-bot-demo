# 筆記

## QR code
Line ID @327lmcvf<br>
<img src="src/main/resources/static/img/QR_code.png" width="200">


## Line Message API
### 運作方式

使用https資料型態為json
<img src="src/main/resources/static/img/1.png">

## 
1. 要能做到在line發送訊息時 server能依照發送的訊息做不同的處理
2. 能主動從server發送訊息給user(推播)

# 功能

## 關鍵字

### 註冊關鍵字

說明：會將註冊關鍵字到個人記錄裡，之後每當使用者輸入關鍵字，會顯示公車到站資訊。<br>

語法：註冊 keyword station bus1(optional),...<br>

範例1：<code>註冊 回家 三民國中</code> 顯示三民國中所有公車到站<br>

範例2：<code>註冊 回家 三民國中 630,617,645</code> 顯示三民國中 630、617、645到站資訊<br>

若要在已註冊的關鍵字新增公車只要在輸入欲新增的公車即可<br>

<code>註冊 回家 三民國中 紅32</code><br>

這樣輸入回家時 就會顯示三民國中 630、617、645、紅32的到站資訊<br>

若要取消公車則請參考下方取消關鍵字刪除該關鍵字在重新創立

### 取消關鍵字

說明：將已輸入的關鍵字刪除

語法：刪除 keyword<br>

範例1：<code>刪除 回家</code><br>

### 查詢關鍵字

說明：查詢當前個人所有已輸入的關鍵字<br>

語法：查詢關鍵字

範例1：<code>查詢關鍵字</code><br>

## 即時到站通知
開發中
短期訂閱(當指定公車即將到達時主動發line通知)



### Reference

message api doc<br>
https://developers.line.biz/en/reference/messaging-api/#messages