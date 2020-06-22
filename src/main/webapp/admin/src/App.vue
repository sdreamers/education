<template>
    <div id="container">
        <router-view/>
    </div>
</template>

<script>
export default {
    name: 'container',
    created() {
        if (!window.isIE) {
            document.querySelector('.loader').classList.add('hide');
        }
    }
};
</script>

<style lang="less">
html, body {
    height: 100%;
    margin: 0;
    font-family: '-apple-system','Open Sans',HelveticaNeue-Light,'Helvetica Neue Light','Helvetica Neue','Hiragino Sans GB','Microsoft YaHei',Helvetica,Arial,sans-serif;
    // -webkit-font-smoothing: antialiased;
    // -moz-osx-font-smoothing: grayscale;
    color: #2c3e50;
}
#container {
    height: 100%;
    &>.el-container {
        height: 100%;
    }
}
a {
    color: #333;
    text-decoration: none;
}
body {
}
</style>
