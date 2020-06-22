<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
            </div>
            <el-card shadow="none">
                <div slot="header" class="card-title"><i class="el-icon-menu"></i>待我审核</div>
                <el-row class="list-con clearfix">
                        <el-col :span="4"><el-badge :value="info.refundAuditCount" class="item" :type="info.refundAuditCount === 0 ? 'primary' : ''"><router-link :to="{name: '/orderRefund/index'}"><el-button size="small">退货审核</el-button></router-link></el-badge></el-col>
                        <el-col :span="4"><el-badge :value="info.rechargeAuditCount" class="item" :type="info.rechargeAuditCount === 0 ? 'primary' : ''"><router-link :to="{name: '/rechargeOrder/index'}"><el-button size="small">后台充值审核</el-button></router-link></el-badge></el-col>
                </el-row>
            </el-card>
            <el-card shadow="none">
                <div slot="header" class="card-title"><i class="el-icon-menu"></i>未完成订单</div>
                <el-row class="list-con clearfix">
                        <el-col :span="4"><el-badge :value="info.orderWaitPrintCount" class="item" :type="info.orderWaitPrintCount === 0 ? 'primary' : ''"><router-link :to="{name: '/order/print/index'}"><el-button size="small">待打印</el-button></router-link></el-badge></el-col>
                        <el-col :span="4"><el-badge :value="info.orderWaitAssignCount" class="item" :type="info.orderWaitAssignCount === 0 ? 'primary' : ''"><router-link :to="{name: '/order/unassigned/index'}"><el-button size="small">待指派</el-button></router-link></el-badge></el-col>
                        <el-col :span="4"><el-badge :value="info.orderDeliveryCount" class="item" :type="info.orderDeliveryCount === 0 ? 'primary' : ''"><router-link :to="{name: '/order/index'}"><el-button size="small">配送中</el-button></router-link></el-badge></el-col>
                </el-row>
            </el-card>
            <el-card shadow="none">
                <div slot="header" class="card-title"><i class="el-icon-menu"></i>待认证商户</div>
                <el-row class="list-con clearfix">
                        <el-col :span="4"><el-badge :value="info.merchantWaitAuthCount" class="item" :type="info.merchantWaitAuthCount === 0 ? 'primary' : ''"><router-link :to="{name: '/account/merchant/auth/index'}"><el-button size="small">待认证商户</el-button></router-link></el-badge></el-col>
                </el-row>
            </el-card>
        </el-card>
    </section>
</template>

<script>
    import api from '@/api/mywork';

    export default {
        data() {
            return {
                info: {}
            };
        },
        methods: {
            // 获取数据
            countByWorks() {
                api.countByWorks({}).then(res => {
                    if (res.code === 100) {
                        this.info = res.content;
                    }
                });
            }
        },

        created() {
            this.countByWorks();
        }

    };
</script>
