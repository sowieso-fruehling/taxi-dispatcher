#!/usr/bin/env bash
curl -i --header "Accept:application/json" -X DELETE -b scripts/cookie.txt http://localhost:8080/v1/drivers/-4/car