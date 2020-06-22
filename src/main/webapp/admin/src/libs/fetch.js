import axios from 'axios';
import Util from '@/libs/util';

function notify(data) {
    return window.ELEMENT.Notification({
        message: data.msg,
        type: data.type,
        dangerouslyUseHTMLString: data.htmlEnable || false,
        duration: data.duration || 5000
    });
}
// 创建axios实例
const service = axios.create({
    // api的base_url，可以理解为固定前缀
    baseURL: window.vars.URLApiBase,
    // 请求超时时间，给30秒避免有的接口响应太久
    // timeout: 30000,
    //  若跨域，带上 cookie（目前没有，vue 和接口部署在同源）
    withCredentials: true
});

// 全局 get 请求添加时间戳
service.interceptors.request.use(config => {
    // 如果是 get 请求但是没有带参数，给参数一个缺省值，以便加上时间戳避免缓存
    if (config.method === 'get') {
        if (!config.params) {
            config.params = {};
        }
        config.params.timestamp = new Date().getTime();
    }
    return config;
}, error => Promise.reject(error));

// 全局response统一处理
service.interceptors.response.use(
    response => {
        window.$Loading.close();
        // 我们认为所有的 ajax 请求都应该返回 json
        // 如果返回的空字符串，认为是无数据
        // 否则认为发生异常

        // 如果返回的是 json，但是 code 不为100，也认为异常
        if (typeof response.data !== 'object') {
            if (response.data === '') {
                notify({
                    msg: '无数据',
                    type: 'info',
                    duration: 5000
                });
            } else {
                notify({
                    msg: '未知异常',
                    type: 'error',
                    duration: 5000
                });
            }
            // location.hash = '';
            // location.reload();
        } else if (response.data && response.data.code && (response.data.code !== 100 && response.data.code !== '100')) {
            notify({
                msg: response.data.message || '未知异常，请确认输入是否异常，仍无法解决请与技术支持联系。',
                type: 'error',
                duration: 5000
            });
        }
        return response.data;
    },
    error => {
        // 如果该请求返回的 http code 不为200，则会进入这个异常捕获
        // 如果返回了 message，只提示 message
        // 否则提示 http 的错误信息
        window.$Loading.close();
        let errMsg = '';
        try {
            if (error.response.data.message) {
                errMsg = error.response.data.message;
            } else {
                if (error.response.status === 500) {
                    errMsg = '可能暂无这个权限，请联系技术。';
                } else if (error.response.status === 400) {
                    errMsg = '提交的数据有误，请联系技术';
                } else if (error.response.status === 401) {
                    errMsg = '可能暂无这个权限，请联系技术'
                } else if (error.code === 'ECONNABORTED') {
                    errMsg = `接口超时<br>错误地址：<br>${error.config.url}`;
                } else {
                    errMsg = '服务器似乎开了小差：<br>' + error.message + `<br>错误地址：<br>${error.config.url}`;
                }
            }
        } catch (err) {
            // console.log(err);
        }
        console.error(`AJAX错误：${error}`);
        try {
            notify({
                msg: errMsg || '未知异常',
                type: 'error',
                duration: 5000,
                htmlEnable: true
            });
        } catch (error) {
            alert(errMsg);
        }
        return Promise.reject(error);
    }
);

const fetch = data => {
    // 如果 api 里面的 fetch 传了 formData 参数，则处理为 formData 请求
    if (data.formData) {
        data.transformRequest = [function(data) {
            return Util.qs(data);
        }];
    }
    window.$Loading = window.ELEMENT.Loading.service({
        background: 'rgba(0, 0, 0, 0.1)',
        text: '拼命加载中'
    });
    return service(data);
};

export default fetch;
