#/bin/sh
DIR=`dirname $0`

if [ $# -lt 1 ] ; then
  echo "run.sh <env> <other params>"
  exit 1
fi

SERVER_NAME=sofa-jobs-agent-bootstrap


JOB_NAME="NAME"
RUN_ENV=""
if [ $1 = "--run_env" ] ; then
	RUN_ENV=$2
else
    RUN_ENV=$1
fi
echo "RUN_ENV:$RUN_ENV"

shift
shift
PARAMS=$*


JAR_DIR=""
if [ $RUN_ENV = "local" ] ; then
	JAR_DIR=/Users/jimmy/space/tianru/sofa-dashboard/sofa-jobs/sofa-jobs-agent-bootstrap/target
fi
echo "JAR_DIR:$JAR_DIR"


export USER_MEM_ARGS="-Xms64m -Xmx512m"

if [ -f /netpay/bin/env.sh ] ; then
    echo "use /netpay/bin/env.sh"
	. /netpay/bin/env.sh
	export USER_MEM_ARGS="-Xms512m -Xmx2048m"
elif [ -f ~/server/bin/env.sh ] ; then
    echo "use ~/server/bin/env.sh"
	. ~/server/bin/env.sh
	export USER_MEM_ARGS="-Xms64m -Xmx512m"
fi


if [ -d /netpay/lib ] ; then
	export MY_LIBRARY_PATH="/netpay/lib"
elif [ -d /home/netpay/libpath ] ; then
	export MY_LIBRARY_PATH="/home/netpay/libpath"
fi

export JAVA_OPTIONS="${USER_MEM_ARGS} $PARAMS -Djava.awt.headless=true -DSERVER_NAME=$SERVER_NAME -Drun_env=${RUN_ENV} -Dspring.profiles.active=zz,batch,${RUN_ENV} -Dlogback.configurationFile=logback_batch.xml "

#java $JAVA_OPTIONS -cp $CLASSPATH com.chinaums.netpay.jobs.Main $PARAMS

echo ">> run #: java $JAVA_OPTIONS -jar $JAR_DIR/$SERVER_NAME.jar"
# > /dev/null 2>&1 &
java $JAVA_OPTIONS -jar $JAR_DIR/$SERVER_NAME.jar
