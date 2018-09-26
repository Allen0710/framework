--
-- Created by IntelliJ IDEA.
-- User: zhang
-- Date: 2018/9/11
-- Time: 19:42
-- To change this template use File | Settings | File Templates.
--
--lua 下标从 1 开始
-- 限流 key
local key = KEYS[1]
local result=1
redis.pcall("HMSET",key,
    "last_mill_second",ARGV[1],
    "curr_permits",ARGV[2],
    "max_burst",ARGV[3],
    "rate",ARGV[4],
    "app",ARGV[5])
return result

