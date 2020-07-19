import Main from '@/views/main/Main.vue';

// 不作为Main组件的子页面展示的页面单独写，如下
export const loginRouter = {
    path: '/login',
    name: 'login',
    meta: {
        title: '登录'
    },
    component: resolve => {
        require.ensure([], () => resolve(require('@/views/login/Login.vue')), 'login');
    }
};
// 不作为Main组件的子页面展示的页面单独写，如下
export const regRouter = {
    path: '/reg',
    name: 'reg',
    meta: {
        title: '登录'
    },
    component: resolve => {
        require.ensure([], () => resolve(require('@/views/reg/reg.vue')), 'reg');
    }
};

export const redirect = {
    path: '/redirect',
    component: Main,
    // hidden: true,
    children: [{
        path: '/redirect/:path*',
        meta: {
            noCache: true
        },
        name: '/redirect',
        component: resolve => {
            require.ensure([], () => resolve(require('@/views/redirect/index.vue')), 'reg');
        }
    }]
};

// 不作为Main组件的子页面展示的页面单独写，如下
export const helpRouter = {
    path: '/help',
    name: 'help',
    component: Main,
    children: [
        {
            path: 'index',
            title: '帮助',
            meta: {
                title: '帮助'
            },
            name: 'helpIndex',
            component: resolve => {
                require.ensure([], () => resolve(require('@/views/help/index.vue')), 'helpIndex');
            }
        }
    ]
};

export const page404 = {
    path: '*',
    name: 'error',
    meta: {
        title: '页面不存在'
    },
    component: Main,
    children: [
        {
            path: '*', title: '没有权限', name: 'error_404', component: resolve => { require.ensure([], () => resolve(require('@/views/errPages/404.vue')), '404'); }, meta: { title: '没有权限' }
        }
    ]
};

// 首页
export const main = {
    path: '/',
    name: 'index',
    title: '首页',
    meta: { title: '首页' },
    component: Main,
    children: [
        {
            path: '/home', title: '首页', name: 'home', component: resolve => { require.ensure([], () => resolve(require('@/views/home/index.vue')), 'home'); }, meta: { title: '首页' }
        },
        {
            path: '/mywork', title: '我的工作', name: '/myWork/index', component: resolve => { require.ensure([], () => resolve(require('@/views/mywork/index.vue')), 'mywork'); }, meta: { title: '我的工作' }
        }

    ]
};

// 作为Main组件的子页面展示并且在左侧菜单显示的路由写在appRouter里
export const appRouter = [
    {

        path: '/project',
        name: 'project',
        title: '商户列表',
        meta: {
            title: '商户列表'
        },
        component: Main,
        children: [
            {
                path: 'index',
                title: '包进度',
                name: '/project/index',
                meta: {
                    title: '包进度'
                },
                component: resolve => {
                    require.ensure([], () => resolve(require('@/views/project/index.vue')), 'projectIndex');
                }
            }
        ]
    },

    {
        path: '/task',
        name: 'task',
        title: '统计',
        meta: {
            title: ''
        },
        component: Main,
        children: [

            {
                path: 'updatePswd',
                title: '更新密码',
                name: '/user/updatePswd',
                meta: {
                    title: '更新密码'
                },
                component: resolve => {
                    require.ensure([], () => resolve(require('@/views/user/updatePswd.vue')), 'updatePswd');
                }
            },

            {
                path: 'taskIndex',
                title: '供货明细',
                name: '/task/index',
                meta: {
                    title: '供货明细'
                },
                component: resolve => {
                    require.ensure([], () => resolve(require('@/views/task/index.vue')), 'taskIndex');
                }
            },

            {
                path: 'schoolIndex',
                title: '学校进度',
                name: '/school/index',
                meta: {
                    title: '学校进度'
                },
                component: resolve => {
                    require.ensure([], () => resolve(require('@/views/school/index.vue')), 'schoolIndex');
                }
            },

            {
                path: 'devices',
                title: '设备明细',
                name: '/task/devices',
                meta: {
                    title: '设备明细'
                },
                component: resolve => {
                    require.ensure([], () => resolve(require('@/views/task/devices.vue')), 'devicesIndex');
                }
            },

            {
                path: 'schoolDevices',
                title: '设备明细',
                name: '/school/devices',
                meta: {
                    title: '设备明细'
                },
                component: resolve => {
                    require.ensure([], () => resolve(require('@/views/school/devices.vue')), 'schoolDevices');
                }
            },

            {
                path: 'packetDevices',
                title: '设备明细',
                name: '/project/devices',
                meta: {
                    title: '设备明细'
                },
                component: resolve => {
                    require.ensure([], () => resolve(require('@/views/project/devices.vue')), 'packetDevices');
                }
            },
        ]
    }
];

// 所有上面定义的路由都要写在下面的routers里
export const routers = [
    loginRouter,
    regRouter,
    helpRouter,
    main,
    ...appRouter,
    page404,
    redirect
];
