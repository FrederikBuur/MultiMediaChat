# MultiMediaChat

## Install

In app-level gradle file:

```
implementation 'com.github.FrederikBuur:MultiMediaChat:1651829f63'
```

## Setup

Insert inputfield in fragment/activity
```
<fragment
  android:id="@+id/mmInputField"
  tools:layout="@layout/view_mm_input_field"
  android:name="com.buur.frederik.multimediechat.inputfield.MMInputFragment"
  android:layout_width="match_parent"
  android:layout_height="wrap_content" />
```

Implement "ISendMessage" interface in fragment/activity
```
class ChatFragment : ISendMessage {
...
}
```

Override sendMMData method fomr interface:
```
override fun sendMMData(mmData: MMData) {
..
}

```

Setup lib before useable:
```
private fun setupMMLib() {
  mmInputFrag = MMInputFragment.getMMInputFieldInstance(childFragmentManager, R.id.mmInputField)
  mmInputFrag?.setup(this)
}
```

Setup recyclerview adapter to use chat views. ex.:

```
override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {

  return ChatViewHolder(when (viewType) {

    MMDataType.Audio.ordinal -> AudioView(context)
    MMDataType.Video.ordinal -> VideoView(context)
    MMDataType.Image.ordinal, MMDataType.Gif.ordinal -> ImgView(context)
    MMDataType.Text.ordinal -> TextMessageView(context)
    MMDataType.Document.ordinal -> DocumentView(context)
  })
}
```

```
override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

  when (itemView) {
    is SuperView -> {
      itemView.setup(isSender, mmData, previousMMData)
    }
    ...
  }

}
```

[Readme template](https://gist.github.com/PurpleBooth/109311bb0361f32d87a2)
