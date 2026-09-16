"""
脚本1：生成10万条测试用户数据，保存为CSV文件
"""
import pandas as pd
import random
from faker import Faker

# 初始化Faker（中文）
fake = Faker('zh_CN')

# 生成数据
def generate_users(count=100000):
    data = []
    for i in range(1, count + 1):
        data.append({
            'username': f'user_{i}:' + fake.name(),
            'password': '123456',
            'email': fake.email(),          # 随机邮箱
            'age': random.randint(18, 60),
            #'real_name': fake.name(),     # 随机中文姓名
            #'phone': fake.phone_number(), # 随机手机号
            # 'address': fake.address(),    # 随机地址
        })
    df = pd.DataFrame(data)
    df.to_csv('users_100k.csv', index=False, encoding='utf-8-sig')
    print(f"✅ 生成 {len(df)} 条数据")
    return df


if __name__ == '__main__':
    df = generate_users(100000)
    print("\n前5条数据预览：")
    print(df.head())
    print("\n数据统计：")
    print(df.describe())