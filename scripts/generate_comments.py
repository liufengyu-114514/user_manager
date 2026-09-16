# scripts/generate_comments.py
import pandas as pd
import random

data = []
contents = ['很好', '不错', '有帮助', '收藏了', '期待更新', '讲得透彻', '学习了', '感谢分享', '啥也不是', '糟糕透顶']

for i in range(100000):
    data.append({
        'article_id': random.randint(1, 3),
        'user_id': random.randint(1, 100),
        'content': random.choice(contents)
    })

pd.DataFrame(data).to_csv('comments_100k.csv', index=False, encoding='utf-8-sig')
print("✅ 生成10万条评论数据")