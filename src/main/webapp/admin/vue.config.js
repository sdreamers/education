module.exports = {
    devServer: {
        proxy: {
            '/productGroup/pagers': {
                target: 'http://119.23.142.208:8080/admin',
                changeOrigin: true,
                pathRewrite: {
                    '^/productGroup/pagers': '/productGroup/pagers.shtml'
                }
            }
        }
    }
}