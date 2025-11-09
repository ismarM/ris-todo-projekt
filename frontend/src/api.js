// povezava z backendom (Spring Boot na 8080)

import axios from "axios";                          // knjižnica, ki poenostavi delo z api-ji --> nerabiš pisati zahteve fetch()

const api = axios.create({
    baseURL: "http://localhost:8080/api",           // to je moj backend
});

export default api;