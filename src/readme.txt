
[2013.11.09 v1.0.6]
1.公布日期采用detail,list页面中采集到的公布日期中，较晚的一个作为公布日期。
2.extract队列采用效率最高的阻塞队列ArrayBlockingQueue，且容量设置为50
3.线程池拆分为extracter和fetcher两个。
4.忽略detail fetch前的isIgnore检查，每次都进detail抓取详细信息
5.新增两个启动脚本

[2013.09.14 v1.0.5]
重复链接，不同发布日期，冗余记录

[2013.09.10 v1.0.4]
改版，面目全非的改版

[2013.07.11 v1.0.3]
list callback是，公用了detail callback 对象。