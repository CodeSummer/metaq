#!/bin/bash

# �������(Ŀ¼)��ǰ�����ƫ��,һ�����ڷ���Ǩ��ʱ
# usage:
#      MovePartitionFiles -dataDir /home/admin/metadata -topic xxtopic -start 5 -end 10 -offset -5
 
sh $(dirname $0)/run-class.sh com.taobao.metamorphosis.tools.shell.MovePartitionFiles $@ 
