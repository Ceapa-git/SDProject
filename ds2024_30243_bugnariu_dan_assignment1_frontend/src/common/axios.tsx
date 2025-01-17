import axios from "axios";

export const axiosUser = axios.create({
    baseURL: (process.env.REACT_APP_USER_SERVICE_API_URL || "http://localhost:8080/") + "user_management/",
    headers: {
        "Content-Type": "application/json",
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Headers":
            "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With"
    },
    validateStatus: function () {
        return true;
    },
});
export const axiosDevice = axios.create({
    baseURL: (process.env.REACT_APP_DEVICE_SERVICE_API_URL || "http://localhost:8081/") + "device_management/",
    headers: {
        "Content-Type": "application/json",
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Headers":
            "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With"
    },
    validateStatus: function () {
        return true;
    },
});
export const axiosMonitoring = axios.create({
    baseURL: (process.env.REACT_APP_MONITORING_SERVICE_API_URL || "http://localhost:8082/") + "monitoring/",
    headers: {
        "Content-Type": "application/json",
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Headers":
            "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With"
    },
    validateStatus: function () {
        return true;
    },
});
export const axiosChat = axios.create({
    baseURL: (process.env.REACT_APP_CHAT_SERVICE_API_URL || "http://localhost:8083/") + "chat/",
    headers: {
        "Content-Type": "application/json",
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Headers":
            "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With"
    },
    validateStatus: function () {
        return true;
    },
});
