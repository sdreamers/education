<template>
  <el-breadcrumb class="app-breadcrumb" separator="/">
    <!-- <transition-group name="breadcrumb"> -->
      <el-breadcrumb-item v-for="(item,index) in levelList" v-if="item.meta" :key="item.path">
        <span class="no-redirect">{{ (item.meta.title) }}</span>
      </el-breadcrumb-item>
    <!-- </transition-group> -->
  </el-breadcrumb>
</template>

<script>
export default {
    data() {
        return {
            levelList: null
        }
    },
    watch: {
        $route() {
            this.getBreadcrumb()
        }
    },
    created() {
        this.getBreadcrumb()
    },
    methods: {
        getBreadcrumb() {
            const matched = this.$route.matched.filter(item => item.name)
            this.levelList = matched
        }
    }
}
</script>

<style lang="less" scoped>
  .app-breadcrumb.el-breadcrumb {
    display: inline-block;
    font-size: 14px;
    line-height: 50px;
    margin-left: 10px;
    vertical-align: middle;
    .no-redirect {
      color: #fff;
      cursor: text;
    }
  }
</style>