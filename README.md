# framework
基础常用框架封装  
1、framework-common  
1)封装disruptor 使用，提供ArrayBlockQueue和Disruptor简单性能测试对比  
2)封装异步Http调用FutureAsyncHttpClient,开启异步Nio调用  
3）封装单机、分布式限流方案  
4）提供Cache key生成器  
5）封装ContextThreadLocalHolder  
2、framework-database  
1)基于mybatis封装读写分离动态数据源，支持一主多从，EnableDynamicDataSource开启动态数据源配置注解，根据sql为select还是update等更新拆分主从数据源，DataSource注解强制指定数据源。  
首先自动解析sql拆分，其次根据DataSource注解配置。
