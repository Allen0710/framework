--
-- Created by IntelliJ IDEA.
-- User: zhang
-- Date: 2018/9/11
-- Time: 19:42
-- To change this template use File | Settings | File Templates.
--

if redis.call('get', KEYS[1]) == ARGV[1] then
    return redis.call('del', KEYS[1])
else
    return 0
end