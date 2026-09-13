"""
脚本3：从MySQL导出数据到Excel
"""
import pandas as pd
from sqlalchemy import create_engine

DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': 'lfy20021218!',
    'database': 'user_db',
    'charset': 'utf8mb4'
}

def export_to_excel(output_file='users_export.xlsx'):
    engine = create_engine(
        f"mysql+pymysql://{DB_CONFIG['user']}:{DB_CONFIG['password']}"
        f"@{DB_CONFIG['host']}:{DB_CONFIG['port']}/{DB_CONFIG['database']}"
        f"?charset={DB_CONFIG['charset']}"
    )

    # 读取数据
    print("从数据库读取数据...")
    df = pd.read_sql("SELECT id, username, email, age, create_time FROM t_user", engine)
    print(f"共读取 {len(df)} 条数据")

    # 写入Excel（支持多Sheet）
    with pd.ExcelWriter(output_file, engine='openpyxl') as writer:
        # Sheet1：全部数据（只取前1000条，避免文件太大）
        df.head(1000).to_excel(writer, sheet_name='用户数据', index=False)

        # Sheet2：统计信息
        stats = pd.DataFrame({
            '指标': ['总用户数', '平均年龄', '最大年龄', '最小年龄'],
            '值': [len(df), round(df['age'].mean(), 2), df['age'].max(), df['age'].min()]
        })
        stats.to_excel(writer, sheet_name='统计信息', index=False)

        # Sheet3：年龄分布
        age_dist = df.groupby('age').size().reset_index(name='人数')
        age_dist.to_excel(writer, sheet_name='年龄分布', index=False)

    print(f"✅ 导出成功：{output_file}")


if __name__ == '__main__':
    export_to_excel('users_export.xlsx')