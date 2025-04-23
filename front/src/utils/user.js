import {datetime} from "mockjs/src/mock/random/date";

export function getUserStringSex(numSex) {
  if (numSex) {
    if (numSex == 1) {
      return '男'
    } else if (numSex == 2) {
      return '女'
    } else {
      return '未设置'
    }
  }
  return '未设置'
}

export function getUserAge(birthDate) {
  if (birthDate) {
    const now = new Date()
    const nowYear = now.getFullYear()
    return nowYear - new Date(birthDate).getFullYear()
  }
  return '未设置'
}

export function getUserRoleName(roleId) {
  if (roleId) {
    if (roleId == 'ADMIN') {
      return '管理员'
    } else if (roleId == 'STUDENT') {
      return '学生'
    } else if (roleId == 'TEACHER') {
      return '教师'
    }
  }
  return '无权限'
}

const gradeDict = {
  1: '一年级',
  2: '二年级',
  3: '三年级',
  4: '四年级',
  5: '五年级',
  6: '六年级'
}

export function getUserGrade(gradeName) {
  if (gradeDict[gradeName]) {
    return gradeDict[gradeName]
  }
  return '无信息'
}

export function getUserClass(className) {
  if (className && className !== '' && String(className).trim().length > 0) {
    return `${String(className).replace('班', '')}班`
  }
  return '无信息'
}

export function getDefaultPage() {
  return {
    currentPage: 1,
    pageSize: 15,
    total: 0
  }
}

export function generateUserShowInfo(userInfoList) {
  userInfoList.forEach(item => {
    item.sex = getUserStringSex(item.sex)
    item.age = getUserAge(item.birthDate)
    item.roleName = getUserRoleName(item.roleNames[0])
    item.className = getUserClass(item.className)
    item.grade = getUserGrade(item.grade)
  })
  return userInfoList
}

export function generateTestInfoShowInfo(testInfoList) {
  testInfoList.forEach(item => {
    item.testTime = item.testTime.split('T')[0]
  })
  return testInfoList
}
