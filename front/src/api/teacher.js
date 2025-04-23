import request from '@/utils/request'
import { getUserAge, getUserClass, getUserGrade, getUserStringSex } from '@/utils/user';

export function listAllStudents(baseUrl, teacherId, keyword, pageInfo) {
  return request({
    url: `${baseUrl}/list`,
    method: 'get',
    params: { teacherId, keyword, pageInfo }
  })
}

export function generateStudentShowInfo(userInfoList) {
  userInfoList.forEach(item => {
    item.sex = getUserStringSex(item.sex)
    item.age = getUserAge(item.birthDate)
    item.className = getUserClass(item.className)
    item.grade = getUserGrade(item.grade)
  })
  return userInfoList
}

export function addStudentScoreExcelUpload(baseUrl, data) {
  return request({
    url: `${baseUrl}/addStudentScoreByExcel`,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data'  // 必须设置
    }
  })
}

export function downloadAddStudentScoreExcelTemp(baseUrl) {
  return request({
    url: `${baseUrl}/downloadAddStudentScoreExcelTemp`,
    method: 'get',
    responseType: 'blob'
  })
}

export function listAllTestInfo(baseUrl, pageInfo, keyword) {
  return request({
    url: `${baseUrl}/listAllTestInfo`,
    method: 'get',
    params: { pageInfo, keyword }
  })
}

export function addTestInfo(baseUrl, data) {
  return request({
    url: `${baseUrl}/addTestInfo`,
    method: 'post',
    data: data
  })
}

export function deleteTestInfo(baseUrl, data){
  return request({
    url: `${baseUrl}/deleteTestInfo`,
    method: 'post',
    data: data
  })
}

export function getAllTestInfo(baseUrl, data) {
  return request({
    url: `${baseUrl}/getAllTestInfo`,
    method: 'get',
    params: { data }
  })
}

export function getStudentScores(baseUrl, userId) {
  return request({
    url: `${baseUrl}/getStudentScores`,
    method: 'get',
    params: { userId }
  })
}

export function getStudentScoresByTestInfoId(baseUrl, testInfoId) {
  return request({
    url: `${baseUrl}/getStudentScoresByTestInfoId`,
    method: 'get',
    params: { testInfoId }
  })
}

export function deleteStudentScore(baseUrl, data) {
  return request({
    url: `${baseUrl}/deleteStudentScore`,
    method: 'post',
    data: data
  })
}

export function addStudentScore(baseUrl, data) {
  return request({
    url: `${baseUrl}/addStudentScore`,
    method: 'post',
    data: data
  })
}

export function getOneScoreByTestInfoId(baseUrl, userId, testInfoId) {
  return request({
    url: `${baseUrl}/getOneScoreByTestInfoId`,
    method: 'get',
    params: { userId, testInfoId }
  })
}
