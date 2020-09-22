# 分布式唯一ID

## Demo

[http://127.0.0.1:8080/swagger-ui.html](http://127.0.0.1:8080/swagger-ui.html)

* 先執行 __src/main/resources/sql/init.sql__ 初始化資料庫
* 修改 __src/main/resources/application.yml__ 連線資料

## 需求背景

* 唯一性：
* 高效能：避免 __ID__ 的生成效能成為系統的瓶頸
* 任性的ID前綴：需求方喜歡特定前綴作為 __ID__ 開頭 ex: XXX000001

## 設計

* 依賴 __MySQL__ 維護當前序號最大值 (5.7.29)
* 利用 __Row Lock__ 處理序號遞增
* 使用 __[User Defined Variables](https://dev.mysql.com/doc/refman/8.0/en/user-variables.html)__ 取回當前交易最大值，降低 __Row Lock__ 的時間
* 支持數字 __ID__ 和前綴字串 __ID__

![](https://babyblue94520.github.io/buffer-id/images/id.png)


### 連續ID

每秒可產生約1萬 __ID__ ，效能依賴資料庫

![](https://i.imgur.com/T4qV4GZ.png)

__優點__

* 連續，不浪費ID

__缺點__

* 每次訪問資料庫，效能差

__小技巧__

* 可利用不同的前綴，避免競爭相同的鎖


### Single Buffer ID

所有執行緒共用一個 __ID buffer__，效能依賴機器硬體

![](https://babyblue94520.github.io/buffer-id/images/single_buffer.png)


__純數字__

每秒可產生約 1千萬左右 __ID__

__前綴__

每秒可產生約 5百萬左右 __ID__

__優點__

* 短時間內，只會訪問資料庫一次，大幅下降資料庫交易壓力
* 相較於 __Multi Buffer__ 較不浪費 __ID__

__缺點__

* __ID__ 連續性不高
* 系統重啟浪費 __ID__ 數量
* 執行緒需要競爭鎖

### Multi Buffer ID

執行緒各自有 __ID buffer__，效能依賴機器硬體

![](https://babyblue94520.github.io/buffer-id/images/multi_buffer.png)

__純數字__

每秒可產生約 1億~2億5 __ID__

![](https://i.imgur.com/xW7uAKY.png)

__前綴__

![](https://i.imgur.com/4sisz0X.png)

__優點__

* 短時間內，只會訪問資料庫一次，大幅下降資料庫交易壓力
* 利用 __Java ThreadLocal__ 無鎖的方式產生 __ID__，效能極高

__缺點__

* __ID__ 連續性較不高
* 系統重啟非常浪費 __ID__ 數量

__小技巧__

* 自動計算 __10__ 秒內需要多少 __buffer__ 降低資料庫的壓力
