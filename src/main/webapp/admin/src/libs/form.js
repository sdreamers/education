/* 操作form相关方法*/

const form = {};
// 初始化表单值，目前只初始化为‘’
form.initForm = function(form) {
    for (const key in form) {
        if (form[key] === null) continue;
        if (form[key].constructor === Array) {
            form[key] = [];
            continue
        }
        form[key] = '';
    }
};

form.bindForm = (form, data) => {
    for (const key in data) {
        form[key] = data[key];
    }
};


export default form;
