#!/usr/bin/env bash
curl -i --header "Accept:application/json" -X GET -b scripts/cookie.txt http://localhost:8080/internal/v1/drivers