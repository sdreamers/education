/* 操作form相关方法*/

const regx = {};

regx.checkMobile = (rule, value, callback) => {
    console.log(value);
    if (value === '') {
        callback(new Error('为空'));
    } else if (!/^1(3|4|5|6|7|8|9)\d{9}$/.test(value)) {
        callback(new Error('请正确输入手机号码'));
    } else {
        callback();
    }
};


export default regx;
