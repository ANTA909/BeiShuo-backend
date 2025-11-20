from pydantic import BaseModel
from typing import Optional

class InterpretationRequest(BaseModel):
    """生成阐释请求"""
    inscriptionId: int
    text: str
    dynasty: Optional[str] = None

class ChatRequest(BaseModel):
    """AI对话请求"""
    question: str
    inscriptionId: Optional[int] = None

