from pydantic import BaseModel
from typing import Optional

class UserInfoResponse(BaseModel):
    """用户信息响应"""
    id: int
    username: str
    email: str

class LoginResponse(BaseModel):
    """登录响应"""
    token: str
    refreshToken: str
    userInfo: UserInfoResponse

