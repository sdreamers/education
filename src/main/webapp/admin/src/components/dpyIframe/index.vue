<template>
    <iframe :src="url" frameborder="0"></iframe>
</template>
<script>
export default {
    props: ['url']
}
</script>
