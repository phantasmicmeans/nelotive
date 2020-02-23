local current = redis.call('zscore', KEYS[1], ARGV[1])
if current ~= nil then
    redis.call('zincrby', KEYS[1], 1, ARGV[1])
    return true
else
    return false
end