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
FILTER4 = ['gmtCreate', 'gmtModified', 'bizOrderId']

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
    #url = 'http://trade.cacheadmin.taobao.org:9999/tccacheadmin/tpConsole.htm?op=querySingle&id=' + i
    url = 'http://10.232.15.168:8080/tccacheadmin/tpConsole.htm?op=querySingle&id=' + i
    text = urllib.request.urlopen(url).read().decode('gbk')
    jsonobj1 = json_obj(sub_order_str(text, MARKER[0]))
    jsonobj2 = json_obj(sub_order_str(text, MARKER[1]))
    jsonobj3 = json_obj(sub_order_str(text, MARKER[2]))
    for f in FILTER1: del jsonobj1[f]
    for f in FILTER2: del jsonobj2[f]
    for f in FILTER3: del jsonobj3[f]
    return [jsonobj1, jsonobj2, jsonobj3]

def comp_dict(obj1, obj2, marker):
    print('diff on ', marker, 'fields:')
    for key in iter(obj1):
        if key == 'attributes':
            continue
        try:
            val2 = obj2[key]
        except KeyError:
            val2 = ''
        if obj1[key] != val2:
            print('\t', key, '=', obj1[key], '\n\t', key, '=', val2)
    try:
        attr1 = obj1['attributes']
        attr2 = obj2['attributes']
    except KeyError:
        return
    print('diff on ', marker, 'attributes:')
    for key in iter(attr1):
        if key in ['fip', 'pOutId', 'pSubOutId', 'cosys']:
            continue;
        try:
            val2 = attr2[key]
        except KeyError:
            val2 = ''
        if attr1[key] != val2:
            print('\t', key, '=', attr1[key], '\n\t', key, '=', val2)

obj1 = parse2objects(id1)
obj2 = parse2objects(id2)

for i in range(3):
    comp_dict(obj1[i], obj2[i], MARKER[i])

def parse2object2(i):
    url = 'http://tmallbuyadmin.admin.taobao.org/order/bizVerticalQuery.htm?biz_order_id=' + i
    text = urllib.request.urlopen(url).read().decode('gbk')
    stop = text.rindex('<hr/>')
    text = text[text.rindex('[', 0, stop):text.rindex(']', 0, stop)+1].replace("&quot;", "\"")
    obj = json.loads(text)
    for el in obj:
        for f in FILTER4: del el[f]
    return obj
    
k = lambda el: el['valueType']
battr1 = sorted(parse2object2(id1), key=k)
battr2 = sorted(parse2object2(id2), key=k)

for i in range(len(battr1)):
    comp_dict(battr1[i], battr2[i], 'BizAttribute ' + str(i+1))
