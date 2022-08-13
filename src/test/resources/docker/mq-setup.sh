#!/bin/sh

set -e

QMGR=$1
QNAME=$2

/opt/mqm/bin/runmqsc "$QMGR" > /dev/null  <<!!!
DEFINE QLOCAL($QNAME.BACKOUT) REPLACE
DEFINE QLOCAL($QNAME) BOTHRESH(1) BOQNAME($QNAME.BACKOUT) REPLACE
!!!

if [ $? -eq 0 ] ; then
  echo "$QMGR $QNAME is created"
else
  echo "$QMGR $QNAME could not be created"
fi
