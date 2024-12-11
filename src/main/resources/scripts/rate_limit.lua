-- 获取KEY
local key = KEYS[1]
-- 获取ARGV内的参数
local limit = tonumber(ARGV[1])
local period = tonumber(ARGV[2])
local period_time = tonumber(ARGV[3])

-- 获取当前流量大小
local current = tonumber(redis.call('get', key) or "0")

if current + 1 > limit then
    -- 达到限流大小 返回当前流量大小
    return current + 1
else
    -- 没有达到阈值 value + 1
    redis.call("INCRBY", key, 1)
    -- 设置过期时间
    redis.call("EXPIRE", key, period_time)
    return current + 1
end
