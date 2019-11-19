# 分布式唯一ID

## Demo

[http://127.0.0.1:8080/swagger-ui.html](http://127.0.0.1:8080/swagger-ui.html)

* 先執行 __src/main/resources/sql/init.sql__ 初始化資料庫
* 修改 __src/main/resources/application.yml__ 連線資料

## 需求

* 唯一性：
* 高效能：避免 __ID__ 的生成效能成為系統的瓶頸
* 任性的ID前綴：需求方喜歡特定前綴作為 __ID__ 開頭 ex: XXX000001

## 設計

* 依賴 __MySQL__ 維護當前序號最大值
* 利用 __Row Lock__ 處理序號遞增
* 使用 __[User Defined Variables](https://dev.mysql.com/doc/refman/8.0/en/user-variables.html)__ 取回當前交易最大值，降低 __Row Lock__ 的時間
* 支持數字 __ID__ 和前綴字串 __ID__

![](https://i.imgur.com/OsHGBmU.png)


### 連續ID

每秒可產生約1萬~1萬5 __ID__ 

![](https://i.imgur.com/T4qV4GZ.png)

__優點__

* 連續，不浪費ID

__缺點__

* 每次訪問資料庫，效能差

__小技巧__

* 可利用不同的前綴，避免競爭相同的鎖

### Buffer ID

![](https://i.imgur.com/sm9OYA1.png)


每秒可產生約 1億~2億5 __ID__

純數字
![](https://i.imgur.com/xW7uAKY.png)

前綴
![](https://i.imgur.com/4sisz0X.png)

__優點__

* 短時間內，只會訪問資料庫一次，大幅下降資料庫交易壓力
* 效能極高

__缺點__

* __ID__ 連續性不高
* 系統重啟浪費 __ID__ 數量

__小技巧__

* 利用 __Java ThreadLocal__ 達到無鎖 __ID__ 生成
* 自動計算 __10__ 秒內需要多少 __Buffer__ 降低資料庫的壓力