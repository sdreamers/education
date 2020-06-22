<template>
<div class="common-tab">
    <div v-model="activeName" @tab-click="handleClick" class="common-tab-content">
        <div
            v-show="G.permission[item.url.replace('/', '_')]"
            v-for="(item,index) in list"
            :key="index"
            :name="item.url"
            @click="handleClick(item)"
            class="common-tab-item"
            :style="{zIndex: 100 - index}"
            :class="{active: activeName === item.url}">{{item.name}}</div>
    </div>
</div>
</template>
<script>
export default {
    props: ['list'],
    data() {
        return {
            activeName: ''
        };
    },
    watch: {
        $route(newValue) {
            this.activeName = newValue.name;
        }
    },
    methods: {
        handleClick(tab) {
            console.log(tab);
            this.$router.push({ name: tab.url });
        },
        setRoute() {
            const map = {
                '/productGroup/index': '/productCategory/index'
            }
            let name = this.$route.name;
            if (map[name]) {
                name = map[name];
            }
            if (this.$route.params.id === -1 || this.$route.params.id === '-1') {
                name = '/productBrand/index'
            } else if (this.$route.params.id === -2 || this.$route.params.id === '-2') {
                name = '/productMerchantType/index'
            }
            this.activeName = name;
        }
    },
    created() {
        this.setRoute();
    }
};
</script>
<style lang="less">
.common-tab {
    height: 54px;
    position: relative;
}
.common-tab-content {
    background-color: #b0c4de;
    position: absolute;
    left: -20px;
    top: -20px;
    right: -20px;
    overflow: hidden;
}
.common-tab-item {
    display: inline-block;
    padding: 0 20px;
    background-color: #f2f2f2;
    line-height: 40px;
    font-size:14px;
    cursor: pointer;
    position: relative;
    &.active {
        background-color: #fff;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
        z-index: 101;
    }
}
</style>
