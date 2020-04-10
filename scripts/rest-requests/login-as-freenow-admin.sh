#!/usr/bin/env bash
curl -i -X POST -d username=admin -d password=admin123 -c scripts/cookie.txt http://localhost:8080/login