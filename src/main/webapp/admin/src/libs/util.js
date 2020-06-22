Date.prototype.customFormat = function(formatString) {
    let YYYY, MMMM, M, DDDD, D, hhh, h, m, s, ampm, dMod;
    const YY = ((YYYY = this.getFullYear()) + '').slice(-2);
    const MM = (M = this.getMonth() + 1) < 10 ? '0' + M : M;
    if (isNaN(M)) { return ''; }
    const MMM = (MMMM = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'][M - 1]).substring(0, 3);
    const DD = (D = this.getDate()) < 10 ? '0' + D : D;
    const DDD = (DDDD = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'][this.getDay()]).substring(0, 3);
    const th = D >= 10 && D <= 20 ? 'th' : (dMod = D % 10) === 1 ? 'st' : dMod === 2 ? 'nd' : dMod === 3 ? 'rd' : 'th';
    formatString = formatString.replace('#YYYY#', YYYY).replace('#YY#', YY).replace('#MMMM#', MMMM).replace('#MMM#', MMM).replace('#MM#', MM).replace('#M#', M).replace('#DDDD#', DDDD).replace('#DDD#', DDD).replace('#DD#', DD).replace('#D#', D).replace('#th#', th);
    h = hhh = this.getHours();
    if (h === 0) h = 24;
    if (h > 12) h -= 12;
    const hh = h < 10 ? '0' + h : h;
    const hhhh = hhh < 10 ? '0' + hhh : hhh;
    const AMPM = (ampm = hhh < 12 ? '上午' : '下午').toUpperCase();
    const mm = (m = this.getMinutes()) < 10 ? '0' + m : m;
    const ss = (s = this.getSeconds()) < 10 ? '0' + s : s;
    return formatString.replace('#hhhh#', hhhh).replace('#hhh#', hhh).replace('#hh#', hh).replace('#h#', h).replace('#mm#', mm).replace('#m#', m).replace('#ss#', ss).replace('#s#', s).replace('#ampm#', ampm).replace('#AMPM#', AMPM);
};

const util = {};
/**
 * 根据页面name获取title
 * @param {Object} vm Vue对象
 * @param {String} name 页面name
 * @return {String} 页面title
 */
util.getViewTitle = function(vm, name) {
    let title = '';
    vm.$router.options.routes.forEach(ele => {
        if (ele.children) {
            ele.children.forEach(el => {
                if (el.name === name) {
                    title = el.title;
                }
            });
        } else if (ele.name === name) {
            title = ele.title;
        }
    });
    return title;
};
util.getTreeCheckedNodes = function(setting, nodes, checked, results) {
    if (!nodes) return [];
    const childKey = setting.childrenKey;
    const checkedKey = setting.checkedKey;
    results = !results ? [] : results;
    for (let i = 0, l = nodes.length; i < l; i++) {
        if (nodes[i][checkedKey] === checked) {
            results.push(nodes[i]);
        }
        util.getTreeCheckedNodes(setting, nodes[i][childKey], checked, results);
    }
    return results;
};
util.transformTozTreeFormat = function(setting, sNodes) {
    let i, l;
    const key = setting.idKey;
    const parentKey = setting.pIdKey;
    const childKey = setting.childrenKey;
    if (!key || key === '' || !sNodes) return [];
    function isArray(arr) {
        return Object.prototype.toString.apply(arr) === '[object Array]';
    }

    if (isArray(sNodes)) {
        const r = [];
        const tmpMap = {};
        for (i = 0, l = sNodes.length; i < l; i++) {
            tmpMap[sNodes[i][key]] = sNodes[i];
        }
        for (i = 0, l = sNodes.length; i < l; i++) {
            if (tmpMap[sNodes[i][parentKey]] && sNodes[i][key] !== sNodes[i][parentKey]) {
                if (!tmpMap[sNodes[i][parentKey]][childKey]) { tmpMap[sNodes[i][parentKey]][childKey] = []; }
                tmpMap[sNodes[i][parentKey]][childKey].push(sNodes[i]);
            } else {
                r.push(sNodes[i]);
            }
        }
        return r;
    } else {
        return [sNodes];
    }
};
/**
 * 格式化时间
 * @param {Object} now Date实例
 * @return {String} 格式化后的时间(2017-10-24 11:30:00)
 */
util.parseTime = function(now) {
    const year = now.getFullYear();
    let month = now.getMonth() + 1;
    let date = now.getDate();
    let hour = now.getHours();
    let minute = now.getMinutes();
    let second = now.getSeconds();
    if (month < 10) {
        month = `0${month}`;
    }
    if (date < 10) {
        date = `0${date}`;
    }
    if (hour < 10) {
        hour = `0${hour}`;
    }
    if (minute < 10) {
        minute = `0${minute}`;
    }
    if (second < 10) {
        second = `0${second}`;
    }
    return `${year}-${month}-${date} ${hour}:${minute}:${second}`;
};

util.parseDate = function(now) {
    const year = now.getFullYear();
    const month = now.getMonth() + 1;
    const date = now.getDate();
    return `${year}-${month}-${date}`;
};

util.parseTimeStr = function(str) {
    if (str) {
        const date = new Date();
        const hours = parseInt(str.substring(8, 10));
        const minutes = parseInt(str.substring(10, 12));
        const seconds = parseInt(str.substring(12, 14));
        date.setYear(parseInt(str.substring(0, 4)));
        date.setMonth(parseInt(str.substring(4, 6)) - 1, parseInt(str.substring(6, 8)));
        if (hours !== '' && !isNaN(hours)) {
            date.setHours(parseInt(str.substring(8, 10)));
        }
        if (minutes !== '' && !isNaN(minutes)) {
            date.setMinutes(parseInt(str.substring(10, 12)));
        }
        if (seconds !== '' && !isNaN(seconds)) {
            date.setSeconds(parseInt(str.substring(12, 14)));
            return date.customFormat('#YYYY#-#MM#-#DD# #hhh#:#mm#:#ss#');
        }
        return date.customFormat('#YYYY#-#MM#-#DD#');
    } else {
        return ' ';
    }
};

/**
 * 根据提供的参数获取日期 yyyy-mm-dd
 * */
util.getDateSubDays = function(number) {
    const date = new Date();
    date.setTime(date.getTime() - number * 24 * 60 * 60 * 1000);
    const year = date.getFullYear();
    let month = date.getMonth() + 1;
    if (month < 10) {
        month = '0' + month;
    }
    let day = date.getDate();
    if (day < 10) {
        day = '0' + day;
    }
    return year + '-' + month + '-' + day;
};

/**
 * 比较两个字符串时间大小,标准时间格式: YYYY-MM-DD HH:MM:SS
 * @param d1 YYY-MM-DD HH:MM:SS
 * @param d2 YYYY-MM-DD HH:MM:SS
 * @return d1小于等于d2返回true, 否则false
 * */
util.compareDateString = (d1, d2) => new Date(d1.replace(/-/g, '/')) <= new Date(d2.replace(/-/g, '/'));

/**
 * 对字符串日期格式化
 * @param s 日期字符串 如：20180325153032
 * @param f 分割符号
 * @returns {string} 2018-03-25 15:30:32
 * @Demo util.dateStrFormat('20180325','-')  - > 2018-03-25
 */
util.dateStrFormat = (s, f) => {
    let str = '';
    for (let i = 4; i <= s.length; i = i + 2) {
        if (i === 4) {
            str += s.substr(0, i);
            continue;
        }
        if (i > 8) {
            if (i === 10) {
                str += ' ' + s.substr(i - 2, 2);
                continue;
            }
            str += ':' + s.substr(i - 2, 2);
            continue;
        }
        str += f + s.substr(i - 2, 2);
    }
    return str;
};
util.qs = function(data) {
    const ret = [];
    for (const item in data) {
        if (data[item] !== undefined && data[item] !== null) {
            ret.push(encodeURIComponent(item) + '=' + encodeURIComponent(data[item]));
        }
    }
    return ret.join('&');
};
util.formatTime = function(time, isDate) {
    function formatDate(now) {
        if (!now) {
            return '';
        }
        const year = now.getFullYear();
        let month = now.getMonth() + 1;
        if (month < 10) {
            month = '0' + month;
        }
        let date = now.getDate();
        if (date < 10) {
            date = '0' + date;
        }
        let hour = now.getHours();
        if (hour < 10) {
            hour = '0' + hour;
        }
        let minute = now.getMinutes();
        if (minute < 10) {
            minute = '0' + minute;
        }
        let second = now.getSeconds();
        if (second < 10) {
            second = '0' + second;
        }
        if (isDate) {
            return `${year}-${month}-${date}`;
        } else {
            return `${year}-${month}-${date} ${hour}:${minute}:${second}`;
        }
    }
    return formatDate(time);
};
/**
 * 获取cookie
 * @param {String} name cookie的key
 * @return {String} cookie的value
 */
util.getCookie = function(name) {
    const reg = new RegExp('(^| )' + name + '=([^;]*)(;|$)');
    const arr = document.cookie.match(reg);
    if (arr) {
        let str;
        try {
            str = decodeURIComponent(arr[2]);
        } catch (e) {
            str = unescape(arr[2]);
        }
        return str;
    }
    return '';
};
window.getCookie = util.getCookie;
/**
 * 设置cookie
 * @param {String} name cookie的key
 * @param {String} value cookie的value
 * @param {Number} days cookie的有效期
 * @return void
 */
util.setCookie = function(name, value, days) {
    if (days === 0) {
        document.cookie = name + '=' + encodeURIComponent(value);
        return;
    }
    const exp = new Date();
    if (isNaN(days)) {
        days = 30;
    }
    exp.setTime(exp.getTime() + days * 24 * 60 * 60 * 1000);
    document.cookie = name + '=' + encodeURIComponent(value) + '; path=/; expires=' + exp.toGMTString();
};
/**
 * 移除cookie
 * @param {String} name cookie的key
 * @return void
 */
util.delCookie = function(name) {
    const t = new Date();
    t.setTime(t.getTime() - 1);
    const n = util.getCookie(name);
    if (n) {
        document.cookie = name + '=' + n + ';expires=' + t.toGMTString() + '; path=/';
    }
};

// 将数组按照decollator拼接成字符串
util.strAssembling = function(array, decollator) {
    if (array instanceof Array) {
        let i = array.length;
        const len = i;
        let scope = '';
        for (i = 0; i < len; i++) {
            scope += array[i] + decollator;
        }
        scope = scope.substr(0, scope.length - 1);
        return scope;
    } else {
        return array;
    }
};

util.accAdd = function(arg1, arg2) {
    let r1, r2;
    try { r1 = arg1.toString().split('.')[1].length; } catch (e) { r1 = 0; }
    try { r2 = arg2.toString().split('.')[1].length; } catch (e) { r2 = 0; }
    const m = Math.pow(10, Math.max(r1, r2));
    return (arg1 * m + arg2 * m) / m;
};

util.accSub = function(arg1, arg2) {
    let r1, r2;
    try { r1 = arg1.toString().split('.')[1].length; } catch (e) { r1 = 0; }
    try { r2 = arg2.toString().split('.')[1].length; } catch (e) { r2 = 0; }
    const m = Math.pow(10, Math.max(r1, r2));
    const n = r1 >= r2 ? r1 : r2;
    return ((arg1 * m - arg2 * m) / m).toFixed(n);
};

util.keepTwoFloatNum = function(num) {
    if (typeof num !== 'string') {
        num = num + '';
    }
    return num.replace(/[^\d.]/g, '').replace(/^\./g, '').replace(/\.{2,}/g, '.').replace('.', '$#$').replace(/\./g, '').replace('$#$', '.').replace(/^(-)*(\d+)\.(\d\d).*$/, '$1$2.$3');
};

util.precisionMath = function(val, num, truncation) {
    if (num === undefined || !util.isNum(num)) num = 2;
    let m = '1';
    for (let i = 0; i < num; i++) {
        m += 0;
    }
    num = parseInt(m);
    switch (truncation) {
        case 'ceil':// 向上取整
            return Math.ceil(val * num) / num;
        case 'down': // 向下取整
            return Math.floor(val * num) / num;
        default: // 四舍五入
            return Math.round(val * num) / num;
    }
};
util.isNum = function(str) {
    const re = /^[0-9]+.?[0-9]*$/;
    return re.test(str);
};
util.validateHalfAYear = function(startTime, endTime) {
    const periodMillionSeconds = new Date(endTime).getTime() - new Date(startTime).getTime();
    const periodDays = periodMillionSeconds / (24 * 3600 * 1000);
    if (periodDays > 180) {
        return true;
    } else {
        return false;
    }
};

util.gridDate = function(value) {
    if (value == null) {
        return '未支付或者是货到付款';
    }
    const appointDeliveryTime = new Date(value);
    return appointDeliveryTime.customFormat('#YYYY#/#MM#/#DD# #AMPM##hh#:#mm#');
};
util.isTimeLimitExceeded = function(startTime, endTime) {
    if ((endTime - startTime) / (1000 * 60 * 60 * 24) > 30) {
        return true;
    } else {
        return false;
    }
}

window.dpy = util;



export default util;
