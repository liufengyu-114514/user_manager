"""
脚本2：将CSV数据批量导入MySQL
"""
import pandas as pd
from sqlalchemy import create_engine
import pymysql

# 数据库配置
DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': 'lfy20021218!',  # 改成你的密码
    'database': 'user_db',
    'charset': 'utf8mb4'
}

def import_csv_to_mysql(csv_file='users_100k.csv'):
    print(f"读取CSV文件：{csv_file}")
    df = pd.read_csv(csv_file)
    print(f"共 {len(df)} 条数据")

    # 创建数据库连接
    engine = create_engine(
        f"mysql+pymysql://{DB_CONFIG['user']}:{DB_CONFIG['password']}"
        f"@{DB_CONFIG['host']}:{DB_CONFIG['port']}/{DB_CONFIG['database']}"
        f"?charset={DB_CONFIG['charset']}"
    )

    # 批量导入（chunksize 控制每次插入的行数）
    print("开始导入...")
    df.to_sql(
        name='t_user',        # 表名
        con=engine,           # 连接
        if_exists='append',   # 追加模式（不覆盖）
        index=False,          # 不把DataFrame索引作为列
        chunksize=5000,       # 每批5000条
        method='multi'        # 多值INSERT，速度快
    )
    print(f"✅ 成功导入 {len(df)} 条数据到 t_user 表")


if __name__ == '__main__':
    import_csv_to_mysql('users_100k.csv')