# RichEditText
多功能文本编辑器
### 各种字体大小、字体颜色、字体类型、URL链接、图片的插入

![image](https://github.com/lwq0739/RichEditText/blob/master/Screenshot/Screenshot_1.png)

### 将数据转化json与解析json成相应文章
这里只展示部分截取的json
`
[
    {
        "textData": {
            "paintBrush": {
                "textColor": -256,
                "textSize": 22,
                "typeface": 1
            },
            "text": " ",
            "url": ""
        },
        "type": 1
    },
    {
        "textData": {
            "paintBrush": {
                "textColor": -65536,
                "textSize": 8,
                "typeface": 3
            },
            "text": "哦",
            "url": ""
        },
        "type": 1
    },
    {
        "textData": {
            "paintBrush": {
                "textColor": -12566464,
                "textSize": 18,
                "typeface": 2
            },
            "text": "链",
            "url": "tel:123466799"
        },
        "type": 1
    },
    {
        "textData": {
            "paintBrush": {
                "textColor": -12566464,
                "textSize": 18,
                "typeface": 2
            },
            "text": "接",
            "url": "tel:123466799"
        },
        "type": 1
    }
]
`

### 使用
`
<ScrollView
        android:layout_marginTop="30dp"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginBottom="45dp">

        <com.lwq.richedittext.super_editext.SuperEditText
            android:id="@+id/richET"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginEnd="5dp"
            android:layout_marginStart="5dp" />
</ScrollView>
`
