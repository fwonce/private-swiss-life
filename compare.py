import sys
import urllib.request
import re
import json

#debug
if len(sys.argv) <= 2:
    id1 = '163044633522346'
    id2 = '162973550142346'
else:
    id1 = sys.argv[len(sys.argv) - 2]
    id2 = sys.argv[len(sys.argv) - 1]

REPLACEMENTS = dict([('\n', ''), ('\t', ''), ('&#034;', '\"')])
MARKER = ['BizOrder', 'PayOrder', 'LogisticsOrder']
FILTER1 = ['gmtCreate', 'gmtModified', 'parentId', 'outOrderId', 'bizOrderId', 'subBizType', 'snapPath', 'ip']
FILTER2 = ['gmtCreate', 'gmtModified', 'payOrderId', 'outPayId']
FILTER3 = ['gmtCreate', 'gmtModified', 'logisticsOrderId']

r = re.compile('|'.join(REPLACEMENTS.keys()))

def replacer(m):
    return REPLACEMENTS[m.group(0)]
    
def sub_order_str(text, marker):
    i = text.index(marker)
    subs = text[i:text.index('</div>', i)]
    return subs[subs.index('{'):]

def json_obj(str):
    jsonstr = r.sub(replacer, str)
    return json.loads(re.sub('([a-zA-Z]+)=', '"\\1":', jsonstr))

def parse2objects(i):
    url = 'http://trade.cacheadmin.taobao.org:9999/tccacheadmin/tpConsole.htm?op=querySingle&id=' + i
    text = urllib.request.urlopen(url).read().decode('gbk')
    jsonobj1 = json_obj(sub_order_str(text, MARKER[0]))
    jsonobj2 = json_obj(sub_order_str(text, MARKER[1]))
    jsonobj3 = json_obj(sub_order_str(text, MARKER[2]))
    for f in FILTER1: del jsonobj1[f]
    for f in FILTER2: del jsonobj2[f]
    for f in FILTER3: del jsonobj3[f]
    return [jsonobj1, jsonobj2, jsonobj3]

obj1 = parse2objects(id1)
obj2 = parse2objects(id2)

for i in range(3):
    print('diff in ', MARKER[i], 'fields:')
    for key in iter(obj1[i]):
        if key == 'attributes':
            continue
        try:
            val2 = obj2[i][key]
        except KeyError:
            val2 = ''
        if obj1[i][key] != val2:
            print('\t', key, '=', obj1[i][key], '\n\t', key, '=', val2)

    try:
        attr1 = obj1[i]['attributes']
        attr2 = obj2[i]['attributes']
    except KeyError:
        continue
    print('diff in ', MARKER[i], 'attributes:')
    for key in iter(attr1):
        if key in ['fip', 'pOutId', 'pSubOutId', 'cosys']:
            continue;
        try:
            val2 = attr2[key]
        except KeyError:
            val2 = ''
        if attr1[key] != val2:
            print('\t', key, '=', attr1[key], '\n\t', key, '=', val2)
