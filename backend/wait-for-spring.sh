#!/bin/sh

until nc -z 127.0.0.1 8080 2>/dev/null; do
  sleep 0.1
done

exec /usr/bin/socat STDIO TCP:127.0.0.1:8080
