from typing import Optional, Any, Generic, TypeVar
from pydantic import BaseModel
from app.common.result_code import ResultCode

T = TypeVar('T')

class Result(BaseModel, Generic[T]):
    """统一响应格式"""
    code: int
    message: str
    data: Optional[T] = None
    timestamp: int = 0
    
    @classmethod
    def success(cls, data: Optional[T] = None, message: str = "success") -> "Result[T]":
        """成功响应"""
        import time
        return cls(
            code=ResultCode.SUCCESS.code,
            message=message,
            data=data,
            timestamp=int(time.time() * 1000)
        )
    
    @classmethod
    def error(cls, result_code: ResultCode, message: Optional[str] = None) -> "Result[None]":
        """错误响应"""
        import time
        return cls(
            code=result_code.code,
            message=message or result_code.message,
            data=None,
            timestamp=int(time.time() * 1000)
        )
    
    @classmethod
    def error_with_code(cls, code: int, message: str) -> "Result[None]":
        """自定义错误响应"""
        import time
        return cls(
            code=code,
            message=message,
            data=None,
            timestamp=int(time.time() * 1000)
        )

