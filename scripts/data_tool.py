"""
数据工具：生成、导入、导出
用法：
    python3 data_tool.py generate --count 100000
    python3 data_tool.py import --file users_100k.csv
    python3 data_tool.py export --output users.xlsx
"""
import argparse
import pandas as pd
import random
from sqlalchemy import create_engine
from faker import Faker
import string


DB_CONFIG = {
    'host': 'localhost', 'port': 3306,
    'user': 'root', 'password': 'lfy20021218!',
    'database': 'user_db', 'charset': 'utf8mb4'
}

fake = Faker('zh_CN')

def get_engine():
    return create_engine(
        f"mysql+pymysql://{DB_CONFIG['user']}:{DB_CONFIG['password']}"
        f"@{DB_CONFIG['host']}:{DB_CONFIG['port']}/{DB_CONFIG['database']}"
        f"?charset={DB_CONFIG['charset']}"
    )

def generate_random_password(length=6):
    """生成随机6位密码（数字+字母）"""
    chars = string.digits + string.ascii_letters
    return ''.join(random.choice(chars) for _ in range(length))

def cmd_generate(args):
    data = [{
        'username': f'user_{i}:' + fake.name(),
        'password': generate_random_password(),
        'email': f'user{i}@example.com',
        'age': random.randint(18, 60)
    } for i in range(1, args.count + 1)]
    pd.DataFrame(data).to_csv(args.output, index=False, encoding='utf-8-sig')
    print(f"✅ 生成 {args.count} 条 → {args.output}")

def cmd_import(args):
    df = pd.read_csv(args.file)
    df.to_sql('t_user', get_engine(), if_exists='append',
              index=False, chunksize=5000, method='multi')
    print(f"✅ 导入 {len(df)} 条")

def cmd_export(args):
    df = pd.read_sql("SELECT * FROM t_user", get_engine())
    df.to_excel(args.output, index=False, engine='openpyxl')
    print(f"✅ 导出 {len(df)} 条 → {args.output}")

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='数据工具')
    sub = parser.add_subparsers(dest='command')

    p1 = sub.add_parser('generate')
    p1.add_argument('--count', type=int, default=100000)
    p1.add_argument('--output', default='users.csv')
    p1.set_defaults(func=cmd_generate)

    p2 = sub.add_parser('import')
    p2.add_argument('--file', required=True)
    p2.set_defaults(func=cmd_import)

    p3 = sub.add_parser('export')
    p3.add_argument('--output', default='users.xlsx')
    p3.set_defaults(func=cmd_export)

    args = parser.parse_args()
    if args.command:
        args.func(args)
    else:
        parser.print_help()