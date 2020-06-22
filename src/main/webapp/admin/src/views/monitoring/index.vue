<template>
<div>
    <dpyIframe :url="url" width="100%" :height="iframeH"></dpyIframe>
</div>
</template>
<script>
import dpyIframe from '@/components/dpyIframe';
export default {
    components: {
        dpyIframe
    },
    data() {
        const wH = window.innerHeight;
        const iframeH = (wH - 40 - 20 - 20) + 'px';
        const url = this.$route.name;
        return {
            url: vars.URLApiBase + url,
            iframeH: iframeH
        }
    }
}
</script>
