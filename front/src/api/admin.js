import request from '@/utils/request'
import {data} from "autoprefixer";

export function listAllUsers(baseUrl, data) {
  return request({
    url: `${baseUrl}/listAllUser`,
    method: 'get',
    params: data
  })
}

export function getInfo(token) {
  return request({
    url: '/user/info',
    method: 'get',
    params: { token }
  })
}

export function changeUserStatus(baseUrl, data) {
  return request({
    url: `${baseUrl}/changeUserStatus`,
    method: 'post',
    data
  })
}

export function deleteUser(baseUrl, data) {
  return request({
    url: `${baseUrl}/deleteUser`,
    method: 'post',
    data
  })
}

export function deleteBunchUser(baseUrl, data) {
  return request({
    url: `${baseUrl}/deleteBunchUser`,
    method: 'post',
    data
  })
}

// 上传文件接口，data参数为file文件
export function addUserExcelUpload(baseUrl, data) {
  return request({
    url: `${baseUrl}/addUserByExcel`,
    method: 'post',
    data: data
  })
}


export function downloadAddUserExcelTemp(baseUrl) {
  return request({
    url: `${baseUrl}/downloadAddUserExcelTemp`,
    method: 'get',
    responseType: 'blob'
  })
}

export function searchUser(baseUrl, keyword, pageInfo) {
  return request({
    url: `${baseUrl}/search`,
    method: 'get',
    params: { keyword, pageInfo }
  })
}

export function resetUserLoginPassword(baseUrl, data){
  return request({
    url: `${baseUrl}/resetUserLoginPassword`,
    method: 'post',
    data
  })
}

export function updateUserPassword(baseUrl, data) {
  return request({
    url: `${baseUrl}/updateUserPassword`,
    method: 'post',
    data
  })
}

export function exportUserInfo(baseUrl) {
  return request({
    url: `${baseUrl}/exportUserInfo`,
    method: 'get'
  })
}
