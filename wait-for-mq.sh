function waitForMQStartupToComplete() {
    echo "NOTE: Checking if MQ is running"

    COUNT=0;
    while [ $(docker logs docker_mq_1 | grep -c "AMQ5041I: The queue manager task 'AUTOCONFIG' has ended.") -eq 0 ]; do
      if [[ ${COUNT} -gt 40 ]]; then exit 1; fi
      echo "NOTE: Waiting for MQ to start..."
      COUNT=$((COUNT + 1))
      sleep 3
    done;

    echo "NOTE: MQ successfully started";
}

waitForMQStartupToComplete
