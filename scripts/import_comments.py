# scripts/import_comments.py
import pandas as pd
from sqlalchemy import create_engine

DB_CONFIG = {
    'host': 'localhost', 'port': 3306,
    'user': 'root', 'password': 'lfy20021218!',
    'database': 'user_db', 'charset': 'utf8mb4'
}

df = pd.read_csv('comments_100k.csv')
engine = create_engine(
    f"mysql+pymysql://{DB_CONFIG['user']}:{DB_CONFIG['password']}"
    f"@{DB_CONFIG['host']}:{DB_CONFIG['port']}/{DB_CONFIG['database']}"
    f"?charset={DB_CONFIG['charset']}"
)
df.to_sql('t_comment', engine, if_exists='append', index=False, chunksize=5000, method='multi')
print(f"✅ 导入 {len(df)} 条评论")