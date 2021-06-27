#!/usr/bin/env bash

set -e

composeFile="./src/test/resources/docker/docker-compose.yml"

reset='\x1B[0m' # No Color/Format
bold='\x1B[1m'
red='\x1B[91m' # Red
green='\x1B[92m' # Green

# Initialise environment settings
if [ $(uname -s) == "Darwin" ]; then
  export macos=true
else
  export macos=false
fi
cd "$(dirname "$0")"

waiting() {
  PID=$1

  echo -n "[-]"

  while kill -0 "$PID" >/dev/null 2>&1; do
    sleep 0.1
    echo -n -e "\b\b\\]"
    sleep 0.1
    echo -n -e "\b\b|]"
    sleep 0.1
    echo -n -e "\b\b/]"
    sleep 0.1
    echo -n -e "\b\b-]"
  done

  echo -e "\b\b${green}${bold}Done${reset}]"
}

setup() {
  echo -n "  .. Building containers... "
  docker-compose --env-file .env -f $composeFile up -d > docker.log 2>&1 &
  PID=$!

  waiting $PID

  echo -n "  .. Configuring containers... "
  ./wait-for-mq.sh >> docker.log 2>&1 &
  PID=$!

  waiting $PID
}

teardown() {
  echo -n "  .. Tearing Down containers... "
  docker-compose --env-file .env -f $composeFile down --volumes --remove-orphans > docker.log 2>&1 &
  PID=$!

  waiting $PID
}

purge() {
  echo -n "  .. Purge containers... "
  docker-compose -f $composeFile down --volumes --remove-orphans --rmi all
  PID=$!

  waiting $PID
}

status() {
  echo "  .. Docker Status ..."
  docker ps -a --format "\t|{{.Names}}\t|{{.Status}}|"
}

build() {
  echo -n "  .. Full Build ... "
  ./mvnw clean install > mvn.log 2>&1 &
  PID=$!

  waiting $PID

  checkMvnLog
}

testOnly() {
  echo -n "  .. Running Tests... "
  ./mvnw clean verify > mvn.log 2>&1 &
  PID=$!

  waiting $PID

  checkMvnLog
}

checkMvnLog() {
  echo ""
  echo -n "    =>   ["
  if grep -q "BUILD SUCCESS" mvn.log
  then
    echo -ne "${green}${bold}SUCCESS${reset}"
  else
    echo -ne "${red}${bold}FAIL${reset}"
    grep "^\[ERROR\]" mvn.log
  fi
  echo "]"
}

cli() {
  while true
  do

    echo -n "  >> "
    read -r com

    case "$com" in

      "setup" | "up")
        setup
        ;;
      "teardown" | "down")
        teardown
        ;;
      "purge")
        purge
        ;;
      "status")
        status
        ;;
      "build" | "b")
        build
        ;;
      "test" | "t")
        testOnly
        ;;
      "quit" | "q")
        exit 0
        ;;
      *)
        echo "  ----------------"
        echo "  ---   Help   ---"
        echo "  ----------------"
        echo "   setup (up)      --> Setup dependencies"
        echo "   teardown (down) --> Teardown dependencies"
        echo -e "   purge           --> Teardown + remove images (${red}WARNING${reset}: removes ALL images)"
        echo "   status          --> Report status of dependencies"
        echo "   build (b)       --> Build the project"
        echo "   test (t)        --> Test the project"
        echo "   quit (q)        --> Exit cli"
        echo "  ----------------"
    esac

  done
}

case "$1" in
  "setup")
    setup false
    ;;
  "refresh")
    setup true
    ;;
  "teardown")
    teardown
    ;;
  "purge")
    purge
    ;;
  "status")
    status
    ;;
  "help")
    echo "------------------------------------------------"
    echo "run script options:"
    echo "------------------------------------------------"
    echo ""
    echo "  Dependency Management:"
    echo "  ----------------------"
    echo "  $ run.sh setup --> Setup dependencies"
    echo "  $ run.sh teardown --> Teardown dependencies"
    echo "  $ run.sh purge --> Tear down and remove images (WARNING: removes ALL images)"
    echo "  $ run.sh status --> Report status of dependencies"
    echo "  $ run.sh -i --> Interactive CLI"
    exit 1
    ;;
  *)
    cli
    ;;
esac
