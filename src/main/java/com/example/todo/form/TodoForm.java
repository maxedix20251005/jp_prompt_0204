package com.example.todo.form;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ToDo作成/編集に用いるフォームクラス。
 *
 * <p>バリデーションには {@code jakarta.validation} のアノテーションを使用します。</p>
 *
 * @author Codex
 * @version 1.0
 * @since 1.0
 * @see com.example.todo.entity.Todo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoForm {

    /** 作成者（必須、最大50文字）。 */
    @NotBlank(message = "作成者は必須です。")
    @Size(max = 50, message = "作成者は50文字以内で入力してください。")
    private String author;

    /** タイトル（必須、最大100文字）。 */
    @NotBlank(message = "タイトルは必須です。")
    @Size(max = 100, message = "タイトルは100文字以内で入力してください。")
    private String title;

    /** 詳細（任意、最大500文字）。 */
    @Size(max = 500, message = "詳細は500文字以内で入力してください。")
    private String description;

    /** 期限日（本日以降）。 */
    @NotNull(message = "期限日は必須です。")
    @FutureOrPresent(message = "期限日は本日以降の日付を指定してください。")
    private LocalDate dueDate;

    /** 優先度（1〜5）。 */
    @NotNull(message = "優先度は必須です。")
    @Min(value = 1, message = "優先度は1以上で入力してください。")
    @Max(value = 5, message = "優先度は5以下で入力してください。")
    private Integer priority;

    /** 楽観的ロック用バージョン。 */
    private Long version;
}
