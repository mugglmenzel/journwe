#!/bin/sh

S3CMD="../s3cmd/s3cmd"
CONF="--config=../s3cmd/s3cfg"
HEADER=`echo --add-header='Cache-Control:max-age=604800'`
HEADER_GZIP=`echo --add-header='Content-Encoding:gzip'`
EXCLUDE=`echo --exclude '*.gz'`
INCLUDE=`echo --include '*.gz'`
ASSETDIR1="../../code/journwe-webapp/target/scala-2.10/classes/public/"
ASSETDIR2="../../code/journwe-webapp/modules/acl/target/scala-2.10/classes/public/"
ASSETDIR3="../../code/journwe-webapp/modules/admin/target/scala-2.10/classes/public/"
ASSETDIR4="../../code/journwe-webapp/modules/core/target/scala-2.10/classes/public/"
ASSETDIR5="../../code/journwe-webapp/modules/data/target/scala-2.10/classes/public/"
ASSETDIR6="../../code/journwe-webapp/modules/dataCache/target/scala-2.10/classes/public/"
ASSETDIR7="../../code/journwe-webapp/modules/email/target/scala-2.10/classes/public/"
ASSETDIR8="../../code/journwe-webapp/modules/social/target/scala-2.10/classes/public/"
S3BUCKET="s3://journwe-assets/"

echo "gzip all files"
gzip_func () {
  gzip -c "$1" > "$1.gz"
}

export -f gzip_func

find $ASSETDIR1 -type f ! -name '*.gz' -exec sh -c 'gzip_func {}' \;
find $ASSETDIR2 -type f ! -name '*.gz' -exec sh -c 'gzip_func {}' \;
find $ASSETDIR3 -type f ! -name '*.gz' -exec sh -c 'gzip_func {}' \;
find $ASSETDIR4 -type f ! -name '*.gz' -exec sh -c 'gzip_func {}' \;
find $ASSETDIR5 -type f ! -name '*.gz' -exec sh -c 'gzip_func {}' \;
find $ASSETDIR6 -type f ! -name '*.gz' -exec sh -c 'gzip_func {}' \;
find $ASSETDIR7 -type f ! -name '*.gz' -exec sh -c 'gzip_func {}' \;

echo "check md5 hash and uploade changed local assets to s3 bucket now... sorry, this can take a while ..."
echo "checking and uploading $ASSETDIR1"
$S3CMD $CONF sync --recursive $HEADER $EXCLUDE $ASSETDIR1 $S3BUCKET
$S3CMD $CONF sync --recursive $HEADER $HEADER_GZIP $INCLUDE $ASSETDIR1 $S3BUCKET
echo "checking $ASSETDIR2"
$S3CMD $CONF sync --recursive $HEADER $EXCLUDE $ASSETDIR2 $S3BUCKET
$S3CMD $CONF sync --recursive $HEADER $HEADER_GZIP $INCLUDE $ASSETDIR2 $S3BUCKET
echo "checking $ASSETDIR3"
$S3CMD $CONF sync --recursive $HEADER $EXCLUDE $ASSETDIR3 $S3BUCKET
$S3CMD $CONF sync --recursive $HEADER $HEADER_GZIP $INCLUDE $ASSETDIR3 $S3BUCKET
echo "checking $ASSETDIR4"
$S3CMD $CONF sync --recursive $HEADER $EXCLUDE $ASSETDIR4 $S3BUCKET
$S3CMD $CONF sync --recursive $HEADER $HEADER_GZIP $INCLUDE $ASSETDIR4 $S3BUCKET
echo "checking $ASSETDIR5"
$S3CMD $CONF sync --recursive $HEADER $EXCLUDE $ASSETDIR5 $S3BUCKET
$S3CMD $CONF sync --recursive $HEADER $HEADER_GZIP $INCLUDE $ASSETDIR5 $S3BUCKET
echo "checking $ASSETDIR6"
$S3CMD $CONF sync --recursive $HEADER $EXCLUDE $ASSETDIR6 $S3BUCKET
$S3CMD $CONF sync --recursive $HEADER $HEADER_GZIP $INCLUDE $ASSETDIR6 $S3BUCKET
echo "checking $ASSETDIR7"
$S3CMD $CONF sync --recursive $HEADER $EXCLUDE $ASSETDIR7 $S3BUCKET
$S3CMD $CONF sync --recursive $HEADER $HEADER_GZIP $INCLUDE $ASSETDIR7 $S3BUCKET
echo "checking $ASSETDIR8"
$S3CMD $CONF sync --recursive $HEADER $EXCLUDE $ASSETDIR8 $S3BUCKET
$S3CMD $CONF sync --recursive $HEADER $HEADER_GZIP $INCLUDE $ASSETDIR8 $S3BUCKET
echo "make assets accessible to everyone"
$S3CMD $CONF setacl $S3BUCKET --acl-public --recursive
