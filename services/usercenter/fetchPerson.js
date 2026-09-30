import { fetchUserProfile } from './fetchUsercenter';

/** 个人资料（person-info 页）：真实用户数据 */
export function fetchPerson() {
  return fetchUserProfile().then((p) => ({
    avatarUrl: p.avatarUrl || '',
    nickName: p.nickname || '',
    gender: p.gender || 0,
    phoneNumber: p.phone || '',
  }));
}
