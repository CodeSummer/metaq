#!/bin/bash

# ���һ��topic��ĳЩ��������
# usage:
#      DeletePartitionFiles -dataDir /home/admin/metadata -topic xxtopic -start 5 -end 10
 
sh $(dirname $0)/run-class.sh com.taobao.metamorphosis.tools.shell.DeletePartitionFiles $@ 
