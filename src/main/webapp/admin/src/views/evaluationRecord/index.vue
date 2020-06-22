<template>
<div>
    <el-card shadow="none">
        <common :list="headerList"></common>
        <dpyIframe :url="url" width="100%" :height="iframeH"></dpyIframe>
    </el-card>
</div>
</template>
<script>
import dpyIframe from '@/components/dpyIframe';
import common from '@/components/common';
import headerList from '@/config/evaluateList_old';
export default {
    components: {
        dpyIframe,
        common
    },
    data() {
        const wH = window.innerHeight;
        const iframeH = (wH - 40 - 20 - 20) + 'px';
        const url = this.$route.name;
        return {
            headerList: headerList,

            url: vars.URLApiBase + url,
            iframeH: iframeH
        }
    }
}
</script>
