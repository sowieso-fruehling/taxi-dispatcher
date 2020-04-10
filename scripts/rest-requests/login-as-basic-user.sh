#!/usr/bin/env bash
curl -i -X POST -d username=user -d password=user123 -c scripts/cookie.txt http://localhost:8080/login