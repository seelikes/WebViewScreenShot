/**
 * Created by liutiantian on 2018/4/17 18:19 星期二
 */

$(document).ready(function() {
    console.log("UL1902LP.DI1211, enter");
    $("#main-content-interface").html(typeof main !== "undefined" ? main.getName() : "MainJs");
});