#/bin/sh
DIR=`dirname $0`

if [ $# -lt 1 ] ; then
  echo "run.sh <env> <other params>"
  exit 1
fi

#定时任务参数里面 第一个是执行的环境 比如 prod 生产一定是prod
JOB_ENV=$1

shift
PARAMS=$*
JOB_NAME="NAME"
if [ $1 = "-execGroovyScript" ] ; then
	JOB_NAME=$2
else
    JOB_NAME=$1
fi
if [ -f /netpay/bin/env.sh ] ; then
    echo "use /netpay/bin/env.sh"
	. /netpay/bin/env.sh
	export USER_MEM_ARGS="-Xms512m -Xmx2048m"
elif [ -f ~/server/bin/env.sh ] ; then
    echo "use ~/server/bin/env.sh"
	. ~/server/bin/env.sh
	export USER_MEM_ARGS="-Xms64m -Xmx512m"
fi

FILE_ENV=$RUN_ENV
echo "file env:$FILE_ENV"
RUN_ENV="$FILE_ENV"


echo "run with env:$RUN_ENV"

if [ -d /netpay/lib ] ; then
	export MY_LIBRARY_PATH="/netpay/lib"
elif [ -d /home/netpay/libpath ] ; then
	export MY_LIBRARY_PATH="/home/netpay/libpath"
fi

export JAVA_OPTIONS="${USER_MEM_ARGS} -Djob_name=${JOB_NAME} -Djava.awt.headless=true -DSERVER_NAME=sofa-jobs-agent-bootstrap -Dproduct_mode=${PRODUCT_MODE} -Dtrace.prefix=${TRACE_PREFIX} -Drun_env=${RUN_ENV} -Dspring.profiles.active=batch,${RUN_ENV} -Dlogback.configurationFile=logback_batch.xml -Djava.library.path=${MY_LIBRARY_PATH} "

JAR_FILES=`find "$DIR/lib" -name "*.jar" | paste -d: -s`

export CLASSPATH="$DIR/classes":$JAR_FILES

java $JAVA_OPTIONS -cp $CLASSPATH com.chinaums.netpay.jobs.Main $PARAMS
nohup java $USER_MEM_ARGS $JAVA_OPTIONS -jar $APP_DIR/$SERVER_NAME.jar  > $LOG_DIR/$SERVER_NAME.log 2>&1 &
